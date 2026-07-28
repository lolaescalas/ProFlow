import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getProjects, createProject } from '../api/projects'
import type { Project } from '../types'
import { useAuth } from '../context/AuthContext'

export default function DashboardPage() {
  const [projects, setProjects] = useState<Project[]>([])
  const [showForm, setShowForm] = useState(false)
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const { logout } = useAuth()
  const navigate = useNavigate()

  useEffect(() => {
    getProjects().then(res => setProjects(res.data))
  }, [])

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault()
    const res = await createProject(name, description)
    setProjects([...projects, res.data])
    setName('')
    setDescription('')
    setShowForm(false)
  }

  return (
    <div className="min-h-screen bg-gray-950 text-white">
      <nav className="bg-gray-900 px-8 py-4 flex justify-between items-center border-b border-gray-800">
        <h1 className="text-xl font-bold text-indigo-400">ProFlow</h1>
        <div className="flex gap-4">
          <button
            onClick={() => navigate('/metrics')}
            className="text-gray-400 hover:text-white text-sm transition"
          >
            Metrics
          </button>
          <button
            onClick={logout}
            className="text-gray-400 hover:text-white text-sm transition"
          >
            Sign out
          </button>
        </div>
      </nav>

      <div className="max-w-4xl mx-auto px-8 py-10">
        <div className="flex justify-between items-center mb-8">
          <h2 className="text-2xl font-semibold">My Projects</h2>
          <button
            onClick={() => setShowForm(!showForm)}
            className="bg-indigo-600 hover:bg-indigo-500 px-4 py-2 rounded-lg text-sm font-semibold transition"
          >
            + New Project
          </button>
        </div>

        {showForm && (
          <form onSubmit={handleCreate} className="bg-gray-900 p-6 rounded-xl mb-6 space-y-3">
            <input
              type="text"
              placeholder="Project name"
              value={name}
              onChange={e => setName(e.target.value)}
              className="w-full bg-gray-800 text-white px-4 py-2 rounded-lg outline-none focus:ring-2 focus:ring-indigo-500"
            />
            <input
              type="text"
              placeholder="Description"
              value={description}
              onChange={e => setDescription(e.target.value)}
              className="w-full bg-gray-800 text-white px-4 py-2 rounded-lg outline-none focus:ring-2 focus:ring-indigo-500"
            />
            <button
              type="submit"
              className="bg-indigo-600 hover:bg-indigo-500 px-4 py-2 rounded-lg text-sm font-semibold transition"
            >
              Create
            </button>
          </form>
        )}

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          {projects.map(project => (
            <div
              key={project.id}
              onClick={() => navigate(`/projects/${project.id}`)}
              className="bg-gray-900 p-6 rounded-xl cursor-pointer hover:ring-2 hover:ring-indigo-500 transition"
            >
              <h3 className="font-semibold text-lg mb-1">{project.name}</h3>
              <p className="text-gray-400 text-sm mb-3">{project.description}</p>
              <p className="text-gray-500 text-xs">Owner: {project.ownerName}</p>
            </div>
          ))}
        </div>

        {projects.length === 0 && (
          <p className="text-gray-500 text-center mt-16">No projects yet. Create your first one!</p>
        )}
      </div>
    </div>
  )
}