import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts'
import { getMetrics } from '../api/metrics'
import type { PerformanceLog } from '../types'

export default function MetricsPage() {
  const [logs, setLogs] = useState<PerformanceLog[]>([])
  const navigate = useNavigate()

  useEffect(() => {
    getMetrics().then(res => setLogs(res.data))
  }, [])

  const summarized = logs.reduce((acc, log) => {
    if (!acc[log.operation]) {
      acc[log.operation] = { operation: log.operation, postgres: [], mongo: [] }
    }
    acc[log.operation].postgres.push(log.postgresTimeMs)
    acc[log.operation].mongo.push(log.mongoTimeMs)
    return acc
  }, {} as Record<string, { operation: string, postgres: number[], mongo: number[] }>)

  const chartData = Object.values(summarized).map(s => ({
    operation: s.operation,
    PostgreSQL: Math.round(s.postgres.reduce((a, b) => a + b, 0) / s.postgres.length),
    MongoDB: Math.round(s.mongo.reduce((a, b) => a + b, 0) / s.mongo.length),
  }))

  return (
    <div className="min-h-screen bg-gray-950 text-white">
      <nav className="bg-gray-900 px-8 py-4 flex justify-between items-center border-b border-gray-800">
        <div className="flex items-center gap-4">
          <button onClick={() => navigate('/dashboard')} className="text-gray-400 hover:text-white text-sm">
            ← Dashboard
          </button>
          <h1 className="text-xl font-bold text-indigo-400">ProFlow</h1>
        </div>
      </nav>

      <div className="max-w-4xl mx-auto px-8 py-10">
        <h2 className="text-2xl font-semibold mb-2">Performance Metrics</h2>
        <p className="text-gray-400 text-sm mb-8">
          Average response time per operation — PostgreSQL vs MongoDB (ms)
        </p>

        {chartData.length > 0 ? (
          <>
            <div className="bg-gray-900 rounded-xl p-6 mb-8">
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={chartData}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
                  <XAxis dataKey="operation" stroke="#9CA3AF" tick={{ fontSize: 12 }} />
                  <YAxis stroke="#9CA3AF" tick={{ fontSize: 12 }} unit="ms" />
                  <Tooltip
                    contentStyle={{ backgroundColor: '#1F2937', border: 'none', borderRadius: '8px' }}
                    labelStyle={{ color: '#F9FAFB' }}
                  />
                  <Legend />
                  <Bar dataKey="PostgreSQL" fill="#6366F1" radius={[4, 4, 0, 0]} />
                  <Bar dataKey="MongoDB" fill="#10B981" radius={[4, 4, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>

            <div className="bg-gray-900 rounded-xl overflow-hidden">
              <table className="w-full text-sm">
                <thead className="bg-gray-800 text-gray-400 uppercase text-xs">
                  <tr>
                    <th className="px-6 py-3 text-left">Operation</th>
                    <th className="px-6 py-3 text-right">PostgreSQL avg</th>
                    <th className="px-6 py-3 text-right">MongoDB avg</th>
                    <th className="px-6 py-3 text-right">Winner</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-800">
                  {chartData.map(row => (
                    <tr key={row.operation}>
                      <td className="px-6 py-4 font-medium">{row.operation}</td>
                      <td className="px-6 py-4 text-right text-indigo-400">{row.PostgreSQL}ms</td>
                      <td className="px-6 py-4 text-right text-emerald-400">{row.MongoDB}ms</td>
                      <td className="px-6 py-4 text-right">
                        {row.PostgreSQL < row.MongoDB
                          ? <span className="text-indigo-400">PostgreSQL ✓</span>
                          : <span className="text-emerald-400">MongoDB ✓</span>
                        }
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </>
        ) : (
          <p className="text-gray-500 text-center mt-16">
            No metrics yet. Create some tasks to generate data.
          </p>
        )}
      </div>
    </div>
  )
}