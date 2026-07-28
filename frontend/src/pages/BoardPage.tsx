import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import {
  DndContext,
  DragOverlay,
  PointerSensor,
  useSensor,
  useSensors,
} from '@dnd-kit/core'
import type { DragEndEvent, DragStartEvent } from '@dnd-kit/core'
import { SortableContext, useSortable, verticalListSortingStrategy } from '@dnd-kit/sortable'
import { CSS } from '@dnd-kit/utilities'
import { getTasks, createTask, updateTaskStatus, deleteTask } from '../api/projects'
import type { Task } from '../types'
import { useAuth } from '../context/AuthContext'

const COLUMNS = ['TODO', 'IN_PROGRESS', 'DONE'] as const
const COLUMN_LABELS: Record<string, string> = {
  TODO: 'To Do',
  IN_PROGRESS: 'In Progress',
  DONE: 'Done',
}

function TaskCard({ task, projectId, onDelete }: { task: Task, projectId: number, onDelete: (id: number) => void }) {
  const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({ id: task.id })

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.4 : 1,
  }

  return (
    <div
      ref={setNodeRef}
      style={style}
      {...attributes}
      {...listeners}
      className="bg-gray-800 p-4 rounded-lg cursor-grab active:cursor-grabbing group"
    >
      <div className="flex justify-between items-start">
        <p className="text-white text-sm font-medium">{task.title}</p>
        <button
          onClick={e => { e.stopPropagation(); onDelete(task.id) }}
          className="text-gray-600 hover:text-red-400 text-xs ml-2 opacity-0 group-hover:opacity-100 transition"
        >
          ✕
        </button>
      </div>
      {task.description && (
        <p className="text-gray-400 text-xs mt-1">{task.description}</p>
      )}
      {task.assigneeName && (
        <p className="text-indigo-400 text-xs mt-2">@{task.assigneeName}</p>
      )}
    </div>
  )
}

export default function BoardPage() {
  const { id } = useParams<{ id: string }>()
  const projectId = Number(id)
  const navigate = useNavigate()
  const { logout } = useAuth()

  const [tasks, setTasks] = useState<Task[]>([])
  const [activeTask, setActiveTask] = useState<Task | null>(null)
  const [showForm, setShowForm] = useState(false)
  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')

  const sensors = useSensors(useSensor(PointerSensor, {
    activationConstraint: { distance: 5 }
  }))

  useEffect(() => {
    getTasks(projectId).then(res => setTasks(res.data))
  }, [projectId])

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault()
    const res = await createTask(projectId, title, description)
    setTasks([...tasks, res.data])
    setTitle('')
    setDescription('')
    setShowForm(false)
  }

  const handleDelete = async (taskId: number) => {
    await deleteTask(projectId, taskId)
    setTasks(tasks.filter(t => t.id !== taskId))
  }

  const handleDragStart = (event: DragStartEvent) => {
    const task = tasks.find(t => t.id === event.active.id)
    if (task) setActiveTask(task)
  }

  const handleDragEnd = async (event: DragEndEvent) => {
    setActiveTask(null)
    const { active, over } = event
    if (!over) return

    const draggedTask = tasks.find(t => t.id === active.id)
    if (!draggedTask) return

    const newStatus = over.id as Task['status']
    if (!COLUMNS.includes(newStatus)) return
    if (draggedTask.status === newStatus) return

    setTasks(tasks.map(t => t.id === draggedTask.id ? { ...t, status: newStatus } : t))
    await updateTaskStatus(projectId, draggedTask.id, newStatus)
  }

  const tasksByStatus = (status: string) => tasks.filter(t => t.status === status)

  return (
    <div className="min-h-screen bg-gray-950 text-white">
      <nav className="bg-gray-900 px-8 py-4 flex justify-between items-center border-b border-gray-800">
        <div className="flex items-center gap-4">
          <button onClick={() => navigate('/dashboard')} className="text-gray-400 hover:text-white text-sm">
            ← Dashboard
          </button>
          <h1 className="text-xl font-bold text-indigo-400">ProFlow</h1>
        </div>
        <div className="flex gap-4">
          <button onClick={() => navigate('/metrics')} className="text-gray-400 hover:text-white text-sm">
            Metrics
          </button>
          <button onClick={logout} className="text-gray-400 hover:text-white text-sm">
            Sign out
          </button>
        </div>
      </nav>

      <div className="px-8 py-6">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-xl font-semibold">Board</h2>
          <button
            onClick={() => setShowForm(!showForm)}
            className="bg-indigo-600 hover:bg-indigo-500 px-4 py-2 rounded-lg text-sm font-semibold transition"
          >
            + New Task
          </button>
        </div>

        {showForm && (
          <form onSubmit={handleCreate} className="bg-gray-900 p-4 rounded-xl mb-6 flex gap-3">
            <input
              type="text"
              placeholder="Task title"
              value={title}
              onChange={e => setTitle(e.target.value)}
              className="flex-1 bg-gray-800 text-white px-4 py-2 rounded-lg outline-none focus:ring-2 focus:ring-indigo-500"
            />
            <input
              type="text"
              placeholder="Description (optional)"
              value={description}
              onChange={e => setDescription(e.target.value)}
              className="flex-1 bg-gray-800 text-white px-4 py-2 rounded-lg outline-none focus:ring-2 focus:ring-indigo-500"
            />
            <button
              type="submit"
              className="bg-indigo-600 hover:bg-indigo-500 px-4 py-2 rounded-lg text-sm font-semibold transition"
            >
              Add
            </button>
          </form>
        )}

        <DndContext sensors={sensors} onDragStart={handleDragStart} onDragEnd={handleDragEnd}>
          <div className="grid grid-cols-3 gap-4">
            {COLUMNS.map(col => (
              <div key={col} className="bg-gray-900 rounded-xl p-4">
                <h3 className="text-sm font-semibold text-gray-400 mb-4 uppercase tracking-wider">
                  {COLUMN_LABELS[col]}
                  <span className="ml-2 bg-gray-800 text-gray-400 text-xs px-2 py-0.5 rounded-full">
                    {tasksByStatus(col).length}
                  </span>
                </h3>
                <SortableContext
                  items={tasksByStatus(col).map(t => t.id)}
                  strategy={verticalListSortingStrategy}
                  id={col}
                >
                  <div
                    id={col}
                    className="space-y-3 min-h-20"
                  >
                    {tasksByStatus(col).map(task => (
                      <TaskCard
                        key={task.id}
                        task={task}
                        projectId={projectId}
                        onDelete={handleDelete}
                      />
                    ))}
                  </div>
                </SortableContext>
              </div>
            ))}
          </div>

          <DragOverlay>
            {activeTask && (
              <div className="bg-gray-800 p-4 rounded-lg shadow-xl opacity-90">
                <p className="text-white text-sm font-medium">{activeTask.title}</p>
              </div>
            )}
          </DragOverlay>
        </DndContext>
      </div>
    </div>
  )
}