import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useGroups } from '../hooks/useGroups';
import { Card } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { Badge } from '../components/ui/Badge';
import { Users, Search, GraduationCap } from 'lucide-react';

export const GroupsPage: React.FC = () => {
  const navigate = useNavigate();
  const { data: groups, isLoading } = useGroups();

  const [search, setSearch] = useState('');
  const [courseFilter, setCourseFilter] = useState<number | null>(null);

  const filtered = groups?.filter((g) => {
    const matchesSearch = g.name.toLowerCase().includes(search.toLowerCase());
    const matchesCourse = courseFilter ? g.course === courseFilter : true;
    return matchesSearch && matchesCourse;
  });

  const courses = [1, 2, 3, 4, 5, 6];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-[var(--color-text)]">Учебные группы</h1>
      </div>

      <div className="flex gap-4">
        <div className="relative flex-1 max-w-sm">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={18} />
          <Input
            placeholder="Поиск группы..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="pl-10"
          />
        </div>
        <div className="flex gap-2">
          <button
            onClick={() => setCourseFilter(null)}
            className={`px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${
              courseFilter === null
                ? 'bg-[var(--color-primary)] text-white'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            }`}
          >
            Все
          </button>
          {courses.map((c) => (
            <button
              key={c}
              onClick={() => setCourseFilter(c)}
              className={`px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${
                courseFilter === c
                  ? 'bg-[var(--color-primary)] text-white'
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            >
              {c} курс
            </button>
          ))}
        </div>
      </div>

      {isLoading ? (
        <div className="text-center py-12 text-gray-500">Загрузка...</div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {filtered?.map((group) => (
            <Card key={group.id} className="cursor-pointer hover:shadow-md transition-shadow" onClick={() => navigate(`/groups/${group.id}`)}>
              <div className="p-4">
                <div className="flex items-center justify-between mb-2">
                  <h3 className="text-lg font-semibold">{group.name}</h3>
                  <Badge variant="info">{group.course} курс</Badge>
                </div>
                <div className="flex items-center gap-4 text-sm text-gray-600">
                  <div className="flex items-center gap-1">
                    <Users size={16} />
                    <span>Студенты</span>
                  </div>
                  <div className="flex items-center gap-1">
                    <GraduationCap size={16} />
                    <span>Направление</span>
                  </div>
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};
