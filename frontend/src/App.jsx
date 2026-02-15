import React, { useState, useRef, useEffect } from 'react';
import { uploadResume, queryResume, getSuggestions } from './services/api';

function App() {
  const [sessionId, setSessionId] = useState(null);
  const [resumeUploaded, setResumeUploaded] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [suggestions, setSuggestions] = useState(null);
  const fileInputRef = useRef(null);
  const messagesEndRef = useRef(null);

  useEffect(() => {
    getSuggestions().then(data => setSuggestions(data));
  }, []);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const handleFileUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    setUploading(true);
    try {
      const response = await uploadResume(file);
      
      if (response.success) {
        setSessionId(response.sessionId);
        setResumeUploaded(true);
        
        setMessages([{
          role: 'assistant',
          content: `${response.summary}\n\nWhat would you like to know?`
        }]);
      } else {
        alert(response.message || 'Failed to upload resume');
      }
    } catch (error) {
      console.error('Upload error:', error);
      alert('Error uploading resume. Please try again.');
    } finally {
      setUploading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!input.trim() || isLoading || !sessionId) return;

    const userMessage = input.trim();
    setInput('');
    setMessages(prev => [...prev, { role: 'user', content: userMessage }]);
    setIsLoading(true);

    try {
      const response = await queryResume(userMessage, sessionId);
      setMessages(prev => [...prev, {
        role: 'assistant',
        content: response.answer
      }]);
    } catch (error) {
      setMessages(prev => [...prev, {
        role: 'assistant',
        content: "I encountered an error. Please try again."
      }]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSuggestionClick = (question) => {
    setInput(question);
  };

  const handleNewResume = () => {
    setResumeUploaded(false);
    setSessionId(null);
    setMessages([]);
    setInput('');
    if (fileInputRef.current) fileInputRef.current.value = '';
  };

  return (
    <div className="min-h-screen bg-gray-50" style={{fontFamily: 'Inter, system-ui, -apple-system, sans-serif'}}>
      {/* Header - Bigger & Stylish */}
      <header className="bg-white border-b border-gray-200 shadow-sm">
        <div className="max-w-7xl mx-auto px-6 py-5">
          <div className="flex items-center justify-between">
            <h1 className="text-3xl font-bold text-gray-900 tracking-tight">Resume AI Agent</h1>
            {resumeUploaded && (
              <button
                onClick={handleNewResume}
                className="text-base font-semibold text-blue-600 hover:text-blue-700 px-4 py-2 rounded-lg hover:bg-blue-50 transition-colors"
              >
                New Resume
              </button>
            )}
          </div>
        </div>
      </header>

      <div className="max-w-7xl mx-auto px-6 py-10">
        {!resumeUploaded ? (
          /* Upload Page - Big & Bold */
          <div className="max-w-3xl mx-auto">
            <div className="text-center mb-10">
              <h2 className="text-5xl font-bold text-gray-900 mb-4 tracking-tight leading-tight">
                Upload Your Resume
              </h2>
              <p className="text-xl text-gray-600 font-medium">
                Get AI-powered interview prep and career insights
              </p>
            </div>

            {/* Upload Box - Bigger */}
            <div className="bg-white border-2 border-gray-300 border-dashed rounded-2xl p-16 hover:border-blue-400 transition-colors">
              <input
                ref={fileInputRef}
                type="file"
                accept=".pdf,.docx"
                onChange={handleFileUpload}
                className="hidden"
                id="resume-upload"
                disabled={uploading}
              />
              
              <label htmlFor="resume-upload" className="block cursor-pointer text-center">
                <div className="mb-6">
                  <p className="text-2xl font-bold text-gray-900 mb-3">
                    {uploading ? 'Processing your resume...' : 'Choose a file or drag here'}
                  </p>
                  <p className="text-lg text-gray-500 font-medium">
                    PDF or DOCX • Max 10MB
                  </p>
                </div>
                
                {!uploading && (
                  <button className="px-8 py-4 bg-blue-600 text-white text-lg font-bold rounded-xl hover:bg-blue-700 transition-colors shadow-lg hover:shadow-xl">
                    Select File
                  </button>
                )}
                
                {uploading && (
                  <div className="text-lg text-gray-600 font-semibold">Please wait...</div>
                )}
              </label>
            </div>

            {/* Feature List - Bigger Text */}
            <div className="mt-12 grid grid-cols-3 gap-6">
              <div className="bg-white border border-gray-200 rounded-xl p-6 text-center hover:shadow-lg transition-shadow">
                <p className="text-lg font-bold text-gray-900 mb-2">Interview Prep</p>
                <p className="text-base text-gray-600">Personalized answers</p>
              </div>
              <div className="bg-white border border-gray-200 rounded-xl p-6 text-center hover:shadow-lg transition-shadow">
                <p className="text-lg font-bold text-gray-900 mb-2">Skills Analysis</p>
                <p className="text-base text-gray-600">Identify strengths</p>
              </div>
              <div className="bg-white border border-gray-200 rounded-xl p-6 text-center hover:shadow-lg transition-shadow">
                <p className="text-lg font-bold text-gray-900 mb-2">Job Matching</p>
                <p className="text-base text-gray-600">Compare to roles</p>
              </div>
            </div>
          </div>
        ) : (
          /* Chat Interface - Bigger Fonts */
          <div className="grid lg:grid-cols-4 gap-6">
            {/* Sidebar - Bigger Text */}
            <div className="lg:col-span-1">
              <div className="bg-white border border-gray-200 rounded-xl p-5 shadow-sm">
                <h3 className="text-xl font-bold text-gray-900 mb-4">Suggestions</h3>
                
                {suggestions && (
                  <div className="space-y-4">
                    <div>
                      <p className="text-sm font-bold text-gray-500 uppercase tracking-wide mb-3">Interview</p>
                      <div className="space-y-2">
                        {suggestions.interview.slice(0, 3).map((q, idx) => (
                          <button
                            key={idx}
                            onClick={() => handleSuggestionClick(q)}
                            className="w-full text-left text-base bg-gray-50 hover:bg-gray-100 px-4 py-3 rounded-lg text-gray-700 font-medium transition-colors"
                          >
                            {q}
                          </button>
                        ))}
                      </div>
                    </div>

                    <div>
                      <p className="text-sm font-bold text-gray-500 uppercase tracking-wide mb-3">Analysis</p>
                      <div className="space-y-2">
                        {suggestions.analysis.slice(0, 3).map((q, idx) => (
                          <button
                            key={idx}
                            onClick={() => handleSuggestionClick(q)}
                            className="w-full text-left text-base bg-gray-50 hover:bg-gray-100 px-4 py-3 rounded-lg text-gray-700 font-medium transition-colors"
                          >
                            {q}
                          </button>
                        ))}
                      </div>
                    </div>
                  </div>
                )}
              </div>
            </div>

            {/* Chat Area - Bigger Messages */}
            <div className="lg:col-span-3">
              <div className="bg-white border border-gray-200 rounded-xl shadow-sm flex flex-col" style={{height: 'calc(100vh - 160px)'}}>
                {/* Messages - Bigger Font */}
                <div className="flex-1 overflow-y-auto p-6 space-y-4">
                  {messages.map((msg, idx) => (
                    <div key={idx} className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}>
                      <div className={`max-w-[80%] px-5 py-4 rounded-2xl shadow-sm ${
                        msg.role === 'user'
                          ? 'bg-blue-600 text-white'
                          : 'bg-gray-100 text-gray-900'
                      }`}>
                        <p className="text-lg whitespace-pre-wrap leading-relaxed font-medium">{msg.content}</p>
                      </div>
                    </div>
                  ))}

                  {isLoading && (
                    <div className="flex justify-start">
                      <div className="bg-gray-100 px-5 py-4 rounded-2xl shadow-sm">
                        <div className="flex space-x-2">
                          <div className="w-3 h-3 bg-gray-400 rounded-full animate-bounce"></div>
                          <div className="w-3 h-3 bg-gray-400 rounded-full animate-bounce" style={{animationDelay: '0.1s'}}></div>
                          <div className="w-3 h-3 bg-gray-400 rounded-full animate-bounce" style={{animationDelay: '0.2s'}}></div>
                        </div>
                      </div>
                    </div>
                  )}

                  <div ref={messagesEndRef} />
                </div>

                {/* Input - Bigger */}
                <div className="border-t border-gray-200 p-5 bg-gray-50">
                  <form onSubmit={handleSubmit} className="flex gap-3">
                    <input
                      type="text"
                      value={input}
                      onChange={(e) => setInput(e.target.value)}
                      placeholder="Ask a question..."
                      className="flex-1 px-5 py-4 text-lg border border-gray-300 rounded-xl focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200 font-medium"
                      disabled={isLoading}
                    />
                    <button
                      type="submit"
                      disabled={isLoading || !input.trim()}
                      className="px-8 py-4 bg-blue-600 text-white text-lg font-bold rounded-xl hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors shadow-md hover:shadow-lg"
                    >
                      Send
                    </button>
                  </form>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Footer - Bigger */}
      <footer className="bg-white border-t border-gray-200 mt-12 shadow-sm">
        <div className="max-w-7xl mx-auto px-6 py-5 text-center">
          <p className="text-base text-gray-600 font-medium">
            Resume AI Agent • Built with Spring Boot & React
          </p>
        </div>
      </footer>
    </div>
  );
}

export default App;