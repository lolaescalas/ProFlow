import client from './client'
import type { Project, Task } from '../types'

export const getProjects = () =>
  client.get<Project[]>('/projects')

export const createProject = (name: string, description: string) =>
  client.post<Project>('/projects', { name, description })

export const getProject = (id: number) =>
  client.get<Project>(`/projects/${id}`)

export const getTasks = (projectId: number) =>
  client.get<Task[]>(`/projects/${projectId}/tasks`)

export const createTask = (projectId: number, title: string, description: string) =>
  client.post<Task>(`/projects/${projectId}/tasks`, { title, description })

export const updateTaskStatus = (projectId: number, taskId: number, status: string) =>
  client.put<Task>(`/projects/${projectId}/tasks/${taskId}/status`, { status })

export const deleteTask = (projectId: number, taskId: number) =>
  client.delete(`/projects/${projectId}/tasks/${taskId}`)