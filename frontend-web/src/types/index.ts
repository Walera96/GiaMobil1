export interface Meeting {
  id: string;
  meetingDate: string;
  startTime?: string;
  endTime?: string;
  location?: string;
  status: 'PLANNED' | 'SCHEDULED' | 'ACTIVE' | 'CLOSED' | 'CANCELLED';
  gekId: string;
  gekName?: string;
  quorumRequired: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface StudentProfile {
  id: string;
  fullName: string;
  recordBookNumber?: string;
  groupName?: string;
  directionCode?: string;
  directionName?: string;
  thesisTopic?: string;
  supervisorName?: string;
  thesisFilePath?: string;
  thesisFileName?: string;
  averageGrade?: number;
}

export interface StudentGrade {
  id: string;
  subjectName: string;
  score: number;
  semester?: string;
}

export interface StudentMeetingInfo {
  meetingId: string;
  meetingDate: string;
  startTime?: string;
  endTime?: string;
  location?: string;
  gekName?: string;
  orderNumber?: number;
}

export interface AgendaItem {
  id: string;
  meetingId: string;
  studentId: string;
  studentFullName: string;
  studentRecordBook?: string;
  thesisTopic?: string;
  supervisorName?: string;
  presentationDuration?: number;
  presentationMaterials?: string;
  averageScore?: number;
  overallAverageScore?: number;
  voteCount?: number;
  thesisFilePath?: string;
  thesisFileName?: string;
}

export interface VoteRequest {
  agendaItemId: string;
  score: number;
  pinCode?: string;
  comment?: string;
}

export interface VoteResponse {
  voteId: string;
  status: string;
  newAverage: number;
  totalVotes: number;
  isFinal: boolean;
}

export interface Vote {
  id: string;
  agendaItemId: string;
  gekMemberId: string;
  gekMemberName?: string;
  score: number;
  comment?: string;
  votedAt: string;
}

export interface Protocol {
  id: string;
  meetingId: string;
  protocolNumber?: string;
  status: 'DRAFT' | 'APPROVED' | 'SIGNED' | 'ARCHIVED';
  generatedAt?: string;
  filePath?: string;
  approvedAt?: string;
  approvedByName?: string;
  createdAt?: string;
}

export interface ProtocolRecord {
  id: string;
  protocolId: string;
  studentId: string;
  studentFullName?: string;
  recordBookNumber?: string;
  scorePoints?: number;
  finalScore?: number;
  isAbsent?: boolean;
  qualification?: string;
  isWithHonors?: boolean;
  decision?: string;
  groupName?: string;
  directionCode?: string;
  directionName?: string;
  createdAt?: string;
}

export interface ScoreSheetRow {
  number: number;
  fullName: string;
  recordBookNumber?: string;
  scorePoints?: number;
  finalScore?: number;
  result: string;
}

export interface ScoreSheetStats {
  totalStudents: number;
  presentCount: number;
  absentCount: number;
  excellentCount: number;
  goodCount: number;
  satisfactoryCount: number;
  unsatisfactoryCount: number;
}

export interface ScoreSheet {
  meetingId: string;
  meetingTitle?: string;
  directionCode?: string;
  directionName?: string;
  groupName?: string;
  rows: ScoreSheetRow[];
  stats: ScoreSheetStats;
}

export interface Admission {
  id: string;
  studentId: string;
  studentFullName: string;
  groupName?: string;
  eligible: boolean;
  reason?: string;
  brsScore?: number;
  hasDebt?: boolean;
}

export interface User {
  id: string;
  username: string;
  fullName: string;
  email?: string;
  role: string;
  active: boolean;
}

export interface GekMember {
  id: string;
  userId: string;
  fullName: string;
  position?: string;
  pinCode?: string;
}

export interface Student {
  id: string;
  firstName: string;
  lastName: string;
  middleName?: string;
  recordBookNumber?: string;
  groupName?: string;
  thesisTopic?: string;
  supervisorName?: string;
}

export interface DraftDocument {
  id: string;
  protocolId: string;
  documentType: 'INDIVIDUAL' | 'FINAL' | 'SCORESHEET';
  content: string;
  status: 'DRAFT' | 'APPROVED';
  createdBy?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface AuditLog {
  id: string;
  tableName: string;
  recordId: string;
  action: 'INSERT' | 'UPDATE' | 'DELETE';
  oldValue?: string;
  newValue?: string;
  changedById?: string;
  ipAddress?: string;
  createdAt: string;
}


export interface Notification {
  id: string;
  title: string;
  message: string;
  type: string;
  read: boolean;
  createdAt: string;
}

