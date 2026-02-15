import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const uploadResume = async (file) => {
  const formData = new FormData();
  formData.append('file', file);

  const response = await axios.post(`${API_BASE_URL}/api/resume/upload`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });

  return response.data;
};

export const queryResume = async (question, sessionId, context = null) => {
  const response = await api.post('/api/resume/query', 
    { question, context },
    {
      headers: {
        'X-Session-ID': sessionId,
      },
    }
  );

  return response.data;
};

export const getSuggestions = async () => {
  const response = await api.get('/api/resume/suggestions');
  return response.data;
};

export const checkHealth = async () => {
  const response = await api.get('/api/resume/health');
  return response.data;
};

export default api;
