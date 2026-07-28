import client from './client'
import type { PerformanceLog } from '../types'

export const getMetrics = () =>
  client.get<PerformanceLog[]>('/metrics')