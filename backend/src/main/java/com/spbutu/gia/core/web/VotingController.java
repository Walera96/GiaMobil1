package com.spbutu.gia.core.web;

import com.spbutu.gia.auth.domain.entity.AppUser;
import com.spbutu.gia.auth.domain.repository.AppUserRepository;
import com.spbutu.gia.core.application.dto.VoteDto;
import com.spbutu.gia.core.application.dto.VoteRequest;
import com.spbutu.gia.core.application.service.VotingService;
import com.spbutu.gia.core.domain.entity.GekMember;
import com.spbutu.gia.core.domain.repository.GekMemberRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST-контроллер голосования членов ГЭК.
 */
@RestController
@RequestMapping("/votes")
public class VotingController {

    private final VotingService votingService;
    private final AppUserRepository appUserRepository;
    private final GekMemberRepository gekMemberRepository;

    public VotingController(VotingService votingService,
                            AppUserRepository appUserRepository,
                            GekMemberRepository gekMemberRepository) {
        this.votingService = votingService;
        this.appUserRepository = appUserRepository;
        this.gekMemberRepository = gekMemberRepository;
    }

    /**
     * Голосование за пункт повестки.
     * POST /api/votes
     */
    @PostMapping
    @PreAuthorize("hasRole('GEK_MEMBER')")
    public ResponseEntity<VoteDto> vote(@RequestBody @Valid VoteRequest request,
                                         @RequestParam(required = false) String pinCode) {
        UUID gekMemberId = getCurrentGekMemberId();
        return ResponseEntity.ok(votingService.vote(gekMemberId, pinCode, request));
    }

    @GetMapping("/agenda-item/{agendaItemId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY', 'CHAIRMAN', 'GEK_MEMBER')")
    public ResponseEntity<List<VoteDto>> getVotesByAgendaItem(@PathVariable UUID agendaItemId) {
        return ResponseEntity.ok(votingService.getVotesByAgendaItem(agendaItemId));
    }

    @GetMapping("/agenda-item/{agendaItemId}/average")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY', 'CHAIRMAN', 'GEK_MEMBER')")
    public ResponseEntity<Integer> getAverageScore(@PathVariable UUID agendaItemId) {
        return ResponseEntity.ok(votingService.calculateAverageScore(agendaItemId));
    }

    @GetMapping("/agenda-item/{agendaItemId}/details")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY', 'CHAIRMAN', 'GEK_MEMBER')")
    public ResponseEntity<Map<String, Object>> getVoteDetails(@PathVariable UUID agendaItemId) {
        List<VoteDto> votes = votingService.getVotesByAgendaItem(agendaItemId);
        Double overallAverage = votingService.getStudentAverageScore(agendaItemId);
        return ResponseEntity.ok(Map.of(
                "agendaItemId", agendaItemId,
                "overallAverageScore", overallAverage != null ? overallAverage : 0.0,
                "totalVotes", votes.size(),
                "votes", votes
        ));
    }

    @PostMapping("/agenda-item/{agendaItemId}/finish")
    @PreAuthorize("hasRole('CHAIRMAN')")
    public ResponseEntity<Map<String, Object>> finishVoting(@PathVariable UUID agendaItemId) {
        return ResponseEntity.ok(votingService.finishVoting(agendaItemId));
    }

    private UUID getCurrentGekMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Пользователь не аутентифицирован");
        }
        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else {
            username = principal.toString();
        }
        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + username));
        GekMember gekMember = gekMemberRepository.findFirstByUserId(appUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Член ГЭК не найден для пользователя: " + username));
        return gekMember.getId();
    }
}
