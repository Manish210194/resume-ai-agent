# 🤖 Resume AI Agent

> An intelligent AI-powered application that analyzes resumes and provides personalized interview preparation, skills analysis, and job matching insights.

[![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green?style=flat-square&logo=spring)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-blue?style=flat-square&logo=react)](https://reactjs.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)](LICENSE)

## 📋 Overview

Resume AI Agent is a full-stack web application that helps job seekers prepare for interviews and understand their professional strengths. Upload your resume (PDF or DOCX), and the AI will:

- 📝 **Generate personalized interview answers** based on your actual experience
- 🎯 **Analyze your skills** and identify key strengths
- 🔍 **Match your resume to job descriptions** with gap analysis
- 💡 **Provide career insights** and positioning strategies



---

## ✨ Features

### 🎤 Interview Preparation
- Generates personalized answers to common interview questions
- Creates compelling "Tell me about yourself" responses
- Provides behavioral question examples using your actual achievements
- Helps you articulate your experience effectively

### 📊 Skills Analysis
- Identifies strongest technical and soft skills
- Quantifies years of experience in each area
- Highlights key achievements and impact metrics
- Suggests areas for skill development

### 🎯 Job Matching
- Compares your resume against job descriptions
- Calculates match percentage with detailed breakdown
- Identifies missing skills and experience gaps
- Provides strategic advice on positioning your candidacy

### 🚀 Technical Features
- **Smart Document Processing**: Extracts text from PDF and DOCX files
- **AI-Powered Analysis**: Uses Google Gemini API for intelligent responses
- **Session Management**: Secure, in-memory resume storage
- **Real-time Chat**: Interactive Q&A interface
- **Responsive Design**: Works on desktop and mobile devices

---

## 🛠️ Tech Stack

### Backend
- **Java 17** - Modern Java features
- **Spring Boot 3.2** - REST API framework
- **Apache PDFBox** - PDF text extraction
- **Apache POI** - DOCX parsing
- **WebFlux** - Async API calls
- **Maven** - Dependency management

### Frontend
- **React 18** - UI framework
- **Vite** - Build tool
- **Tailwind CSS** - Styling
- **Axios** - HTTP client

### AI Integration
- **Google Gemini API** - Natural language processing
- **RAG Pattern** - Context-aware responses

---

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Node.js 18+
- npm 9+
- Google Gemini API key ([Get it free](https://aistudio.google.com/))

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Manish210194/resume-ai-agent.git
   cd resume-ai-agent
   ```

2. **Set up Backend**
   ```bash
   cd backend
   
   # Add your Gemini API key to application.properties
   echo "gemini.api.key=YOUR-API-KEY-HERE" >> src/main/resources/application.properties
   
   # Build and run
   mvn clean install
   mvn spring-boot:run
   ```

3. **Set up Frontend** (in a new terminal)
   ```bash
   cd frontend
   
   # Install dependencies
   npm install
   
   # Start development server
   npm run dev
   ```

4. **Access the application**
   - Open http://localhost:3000 in your browser
   - Upload your resume
   - Start asking questions!

---

## 📖 Usage Examples

### Example Questions You Can Ask:

**Interview Preparation:**
- "How should I answer 'Tell me about yourself'?"
- "What are my key strengths for a Senior Java role?"
- "Help me prepare for 'Why should we hire you?'"

**Skills Analysis:**
- "What are my strongest technical skills?"
- "How many years of experience do I have with Spring Boot?"
- "Summarize my leadership experience"

**Job Matching:**
- "Rate my fit for a Backend Engineer role"
- "Compare my resume to this job description: [paste JD]"
- "What skills am I missing for cloud-native development?"

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────┐
│           React Frontend (Port 3000)        │
│  - File Upload UI                           │
│  - Chat Interface                           │
│  - Suggestion Sidebar                       │
└───────────────┬─────────────────────────────┘
                │ HTTP/REST
                │
┌───────────────▼─────────────────────────────┐
│      Spring Boot Backend (Port 8080)        │
│  ┌─────────────────────────────────────┐   │
│  │  Controllers (REST API)             │   │
│  └────────┬────────────────────────────┘   │
│           │                                 │
│  ┌────────▼────────────────────────────┐   │
│  │  Services (Business Logic)          │   │
│  │  - Resume Storage & Management      │   │
│  │  - AI Integration                   │   │
│  └────────┬────────────────────────────┘   │
│           │                                 │
│  ┌────────▼────────────────────────────┐   │
│  │  Utils (Document Processing)        │   │
│  │  - PDF Parser (Apache PDFBox)       │   │
│  │  - DOCX Parser (Apache POI)         │   │
│  └─────────────────────────────────────┘   │
└───────────────┬─────────────────────────────┘
                │ HTTPS
                │
┌───────────────▼─────────────────────────────┐
│         Google Gemini API                   │
│  - Natural Language Processing              │
│  - Context-Aware Responses                  │
└─────────────────────────────────────────────┘
```

---

## 📁 Project Structure

```
resume-ai-agent/
├── backend/                      # Spring Boot Application
│   ├── src/main/java/
│   │   └── com/manish/resumeai/
│   │       ├── controller/       # REST Controllers
│   │       │   └── ResumeController.java
│   │       ├── service/          # Business Logic
│   │       │   └── ResumeAIService.java
│   │       ├── dto/              # Data Transfer Objects
│   │       │   ├── QueryRequest.java
│   │       │   ├── QueryResponse.java
│   │       │   └── UploadResponse.java
│   │       ├── util/             # Utility Classes
│   │       │   └── ResumeParser.java
│   │       └── ResumeAIApplication.java
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
│
├── frontend/                     # React Application
│   ├── src/
│   │   ├── components/
│   │   ├── services/
│   │   │   └── api.js           # API Client
│   │   ├── App.jsx              # Main Component
│   │   └── main.jsx
│   ├── package.json
│   └── vite.config.js
│
└── README.md
```

---

## 🔒 Security & Privacy

- ✅ **No Data Persistence** - Resumes stored in-memory only
- ✅ **Session Isolation** - Each upload gets unique session ID
- ✅ **No Third-Party Storage** - Data only sent to AI API for processing
- ✅ **Secure API Keys** - Environment variable configuration
- ✅ **CORS Protection** - Configured allowed origins

---

## 🚢 Deployment

### Option 1: Free Hosting (Recommended)

**Backend (Railway):**
1. Push to GitHub
2. Connect Railway to repository
3. Set environment variable: `GEMINI_API_KEY`
4. Deploy automatically

**Frontend (Vercel):**
1. Push to GitHub
2. Import project on Vercel
3. Set `VITE_API_URL` to backend URL
4. Deploy automatically

**Cost:** $0/month

### Option 2: Docker

```bash
# Backend
cd backend
mvn clean package
docker build -t resume-ai-backend .
docker run -p 8080:8080 -e GEMINI_API_KEY=your-key resume-ai-backend

# Frontend
cd frontend
npm run build
docker build -t resume-ai-frontend .
docker run -p 3000:80 resume-ai-frontend
```

---

## 🎯 Use Cases

### For Job Seekers:
- Prepare for upcoming interviews
- Understand your unique value proposition
- Identify skill gaps for target roles
- Practice articulating your experience

### For Career Coaches:
- Help clients understand their strengths
- Generate interview prep materials
- Provide data-driven career advice
- Analyze resume effectiveness

### For Recruiters:
- Quickly understand candidate profiles
- Generate interview questions
- Assess skill-role alignment
- Save time on initial screening

---

## 📈 Future Enhancements

- [ ] Multiple resume comparison
- [ ] Resume optimization suggestions
- [ ] Cover letter generation
- [ ] LinkedIn profile analysis
- [ ] Mock interview mode with scoring
- [ ] Export interview prep to PDF
- [ ] Multi-language support
- [ ] Integration with job boards

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**Manish Kumar Choudhary**

- GitHub: [@Manish210194](https://github.com/Manish210194)
- LinkedIn: [Manish Choudhary](https://linkedin.com/in/manish-choudhary-58242b12a)
- Email: manish.cse15@gmail.com


---

## 🙏 Acknowledgments

- [Google Gemini](https://ai.google.dev/) - AI API
- [Spring Boot](https://spring.io/projects/spring-boot) - Backend framework
- [React](https://reactjs.org/) - Frontend framework
- [Apache PDFBox](https://pdfbox.apache.org/) - PDF processing
- [Apache POI](https://poi.apache.org/) - DOCX processing

---

## 📞 Support

If you have any questions or need help, please:

- Open an [issue](https://github.com/Manish210194/resume-ai-agent/issues)
- Email: manish.cse15@gmail.com
- LinkedIn: [Connect with me](https://linkedin.com/in/manish-choudhary-58242b12a)

---

## ⭐ Show Your Support

If this project helped you, please give it a ⭐️ on GitHub!

---

**Built with ❤️ by Manish Kumar Choudhary**

*Showcasing full-stack development, AI integration, and modern software engineering practices*
