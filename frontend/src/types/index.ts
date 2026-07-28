export interface User {
  id: number
  name: string
  email: string
  role: string
}

export interface AuthResponse {
  token: string
  role: string
}

export interface Project {
  id: number
  name: string
  description: string
  ownerName: string
  memberNames: string[]
  createdAt: string
}

export interface Task {
  id: number
  title: string
  description: string
  status: 'TODO' | 'IN_PROGRESS' | 'DONE'
  assigneeName: string | null
  projectId: number
  createdAt: string
  updatedAt: string
}

export interface PerformanceLog {
  operation: string
  postgresTimeMs: number
  mongoTimeMs: number
  timestamp: string
}