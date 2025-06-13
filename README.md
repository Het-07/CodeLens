<div style="align-items: center;">
   <img src="./frontend/src/assets/file.svg" width="200" height="200" alt="Logo" />
</div>

# CodeLens

CodeLens is a powerful, all-in-one Chrome extension and web application designed to simplify code comprehension and documentation. Leveraging llama model , CodeLens enables users to summarize code snippets, define technical terms, and generate sharable documentation—directly within the browser.

---

## Table of Contents

- [Overview](#overview)
- [Problem Statement](#problem-statement)
- [Features](#features)
- [Tech-Stack](#tech-stack)
- [Dependencies](#dependencies)
- [Installation and Setup](#installation-and-setup)
- [Project Structure](#project-structure)
- [User Manual](#user-manual)
- [API Documentation](#api-documentation)
- [Smell Analysis Report](#smell-analysis-report)
- [TDD Commit Hash](#tdd-commit-hash)
- [Code Coverage](#code-coverage)
- [Contributors](#contributors)
- [User Stories](#user-stories)
- [Acknowledgement](#acknowledgments)

---

## Overview

Modern software development often involves reading unfamiliar code, managing extensive codebases, and communicating complex ideas.
CodeLens addresses these challenges by offering:

- **On-Page Code Summarization:** Quickly convert selected code snippets into human-readable summaries.
- **Instant Term Definitions:** Provide immediate explanations for technical terms for non-technical users.
- **File Upload and Analysis:** Enable users to upload entire code files for structured summarization.
- **Documentation Generation:** Automatically generate and share detailed documentation in multiple formats, including direct integration with Google Docs.

---

## Problem Statement

Developers and non-technical users alike struggle with:

- **Unfamiliar Code:** Understanding and documenting unknown code can be time-consuming and error-prone.
- **Large Codebases:** Legacy or bulk code often needs quick summarization for efficient onboarding.
- **Technical Jargon:** Non-technical team members face challenges with technical terminology.

CodeLens alleviates these pain points by providing an all-in-one solution that automates code summarization, term definition, and documentation generation, ensuring that knowledge is accessible and shareable.

---

## Features

- Login page for both - Extension and Website
- Forgot password functionality
- Dynamic "Summary" button appears when any text is selected for summary
- Error toast notification when selected text exceeds character limit
- Popup suggestion to redirect to the website if text exceeds the limit
- Landing page displaying history tabs post-login
- Input box with file upload for text prompts or file uploads
- Component to display generated responses
- Option to download generated responses as documentation
- Automatically generated shareable documentation links

---

## Tech-Stack

<img src="https://raw.githubusercontent.com/marwin1991/profile-technology-icons/refs/heads/main/icons/html.png" alt="HTML5" height="50" style="margin-left: 10px; margin-right: 10px;" /> 
<img src="https://raw.githubusercontent.com/marwin1991/profile-technology-icons/refs/heads/main/icons/css.png" alt="CSS" height="50" style="margin-left: 10px; margin-right: 10px;" /> 
<img src="https://raw.githubusercontent.com/marwin1991/profile-technology-icons/refs/heads/main/icons/react.png" alt="React" height="50" style="margin-left: 10px; margin-right: 10px;" /> 
<img src="https://raw.githubusercontent.com/marwin1991/profile-technology-icons/refs/heads/main/icons/typescript.png" alt="TypeScript" height="50" style="margin-left: 10px; margin-right: 10px;" /> 
<img src="https://raw.githubusercontent.com/marwin1991/profile-technology-icons/refs/heads/main/icons/spring_boot.png" alt="Spring Boot" height="50" style="margin-left: 10px; margin-right: 10px;" /> 
<img src="https://raw.githubusercontent.com/marwin1991/profile-technology-icons/refs/heads/main/icons/mongodb.png" alt="MongoDB" height="50" style="margin-left: 10px; margin-right: 10px;" /> 
<img src="https://raw.githubusercontent.com/marwin1991/profile-technology-icons/refs/heads/main/icons/docker.png" alt="Docker" height="50" style="margin-left: 10px; margin-right: 10px;" /> 
<img src="https://raw.githubusercontent.com/marwin1991/profile-technology-icons/refs/heads/main/icons/postman.png" alt="Postman" height="50" style="margin-left: 10px; margin-right: 10px;" /> 
<img src="https://raw.githubusercontent.com/marwin1991/profile-technology-icons/refs/heads/main/icons/sonarqube.png" alt="SonarCube" height="50" style="margin-left: 10px; margin-right: 10px;" /> 
<img src="https://raw.githubusercontent.com/marwin1991/profile-technology-icons/refs/heads/main/icons/swagger.png" alt="Swagger" height="50" style="margin-left: 10px; margin-right: 10px;" />

---

## Dependencies

### Frontend Dependencies

#### Core dependencies

- **@reduxjs/toolkit**: ^2.6.0
- **axios**: ^1.7.9
- **primeicons**: ^7.0.0
- **primereact**: ^10.9.2
- **react**: ^18.3.1
- **react-copy-to-clipboard**: ^5.1.0
- **react-dom**: ^18.3.1
- **react-redux**: ^9.2.0

#### Development dependencies

- **@eslint/js**: ^9.17.0
- **@types/react**: ^18.3.18
- **@types/react-copy-to-clipboard**: ^5.0.7
- **@types/react-dom**: ^18.3.5
- **@vitejs/plugin-react**: ^4.3.4
- **eslint**: ^9.17.0
- **eslint-plugin-react-hooks**: ^5.0.0,
- **eslint-plugin-react-refresh**: ^0.4.16
- **globals**: ^15.14.0
- **react-router-dom**: ^7.1.4
- **redux-persist**: ^6.0.0
- **typescript**: ~5.6.2
- **typescript-eslint**: ^8.18.2
- **vite"**: ^6.0.5

### Backend Dependencies

#### Core dependencies

- **spring-boot-starter-web**: ^3.4.2
- **spring-boot-devtools**: ^3.4.2
- **spring-boot-starter-data-mongodb**: ^3.4.2
- **spring-data-mongodb**: ^4.4.1
- **lombok**: ^1.18.36
- **spring-security-web**: ^6.4.2
- **spring-security-config**: ^6.4.2
- **jjwt-api**: ^0.12.6
- **jjwt-impl**: ^0.12.6
- **jjwt-jackson**: ^0.12.6
- **spring-boot-starter-mail**: ^3.4.2
- **jakarta.validation-api**: ^3.1.0
- **spring-boot-starter-validation**: ^3.4.2
- **springdoc-openapi-starter-webmvc-ui**: ^2.8.4
- **poi-ooxml**: ^5.4.0

#### Development dependencies

- **spring-boot-starter-test**: ^3.4.2
- **de.flapdoodle.embed.mongo**: ^4.18.1
- **junit**: ^4.13.2
- **junit-jupiter**: ^1.19.0
- **spring-boot-testcontainers**: ^3.4.2
- **mongo**: ^1.19.0
- **mockito-inline**: ^4.6.0

### Chrome extension

#### Core Dependencies

- **@types/chrome**: ^0.0.301
- **@types/react**: ^19.0.8
- **primeicons**: ^7.0.0
- **primereact**: ^10.9.2
- **react**: ^19.0.0
- **react-dom**: ^19.0.0
- **react-router-dom**: ^7.1.4

---

## Installation and Setup

### CodeLens can be set up in two ways:

- **Using Docker (Recommended)** – Installs all services (frontend, backend, MongoDB, LLaMA model, SonarQube) in one command
- **Without Docker** – Run backend and frontend manually with local MongoDB

## Using Docker (Recommended) 🐳

### 1. Clone the Repository

```bash
   git clone https://github.com/your-username/codelens.git
   cd codelens
```

### 2. Docker Compose (Development Setup)

From the project root, run:

```bash
   docker-compose -f docker/dev/docker-compose.yml up --build
```

This launches:

- **Frontend (React + Vite)** → `http://localhost:3000`
- **Backend (Spring Boot)** → `http://localhost:8080`
- **MongoDB** → `localhost:27017`
- **Mongo Express (GUI)** → `http://localhost:8081`
- **LLaMA API (Ollama)** → `http://localhost:11434`
- **SonarQube** → `http://localhost:9000`

## Without Docker🚀

### 1. Backend Setup

#### 1.1 Start MongoDB locally:

```bash
   sudo service mongod start
```

#### 1.2 Navigate to backend:

```bash
   cd backend
```

#### 1.3 Verify application.properties contains:

```properties
   spring.application.name=backend
   spring.profiles.active=dev
   spring.data.mongodb.repositories.enabled=true
   spring.mail.username=csci5308.group01@gmail.com
   spring.mail.password=pgwtwprvdcashxss
   llama.bot.url=http://localhost:11434/api/generate
```

#### 1.4 Run the Spring Boot app:

```bash
  ./mvnw spring-boot:run
```

➡ Runs at: `http://localhost:8080`

### 2. Frontend Setup

#### 2.1 Navigate to frontend:

```bash
   cd frontend
   npm install
   npm run dev
```

#### 2.2 Add this to frontend/config:

```env
   BASE_URL: `http://localhost:8080/api/v1`
```

➡ Access at: `http://localhost:5173`

### 3. Chrome Extension Setup

1. Open **chrome://extensions**
2. Enable **Developer Mode**
3. Click **"Load Unpacked"**
4. Select the **extension/** directory

### 4. LLM Setup

1. Install Ollama

- For Windows - https://ollama.com/download
- For MacOS - https://ollama.com/download
- For Linux - Execute the installation script

```bash
  curl -fsSL https://ollama.com/install.sh | sh
```

2. Verify

```bash
  ollama --version
```

3. Pull the qwen2.5-coder:0.5b Model

```bash
  ollama pull qwen2.5-coder:0.5b
```

4. Start the Ollama Server

```bash
  ollama serve
```

5. Configure the backend

```bash
  curl -X POST http://localhost:11434/api/generate -d '{ "model": "qwen2.5-coder", "prompt": "Your prompt here" }'
```

### 5. Production Build

Use this if you're deploying the optimized version.

```bash
  docker-compose -f docker/prod/docker-compose.yml up --build
```

➡ Frontend at: `http://localhost:5173`  
➡ Backend at: `http://localhost:8000` (running `prod` profile`)

---

## Project Structure

```bash
  codelens/
  ├── backend/ # Spring Boot backend application
  ├── docker/ # Docker Files for different environment
  ├── docs/ #Documents related to project
  ├── extension/ # Chrome extension
  ├── frontend/ # React frontend application
  ├── .gitlab-ci.yml # CI/CD pipelines
  └── README.md # Project documentation
```

---

## User Manual

[User Manual](./docs/user-manual.md)

---

## API Documentation

[Swagger Docs](http://csci5308-vm1.research.cs.dal.ca:8000/swagger-ui/index.html#/)

---

## Smell Analysis Report

[Smell Analysis Report](https://docs.google.com/spreadsheets/d/1uhpBUSJhWqnZ6DvAtzp6NBU_x_x_ZIbCe3IJgqEHIdc/edit?usp=sharing)

---

## TDD Commit Hash

| Modules       | Before Commit                                                                                                                                      | After Commit                                                                                                                                       |
| ------------- | -------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------- |
| Document      | [bb82a5](https://git.cs.dal.ca/courses/2025-winter/csci-5308/group01/-/merge_requests/66/diffs?commit_id=bb82a535166cd0c17ba093a7f19fa9681e8bc09a) | [f21863](https://git.cs.dal.ca/courses/2025-winter/csci-5308/group01/-/merge_requests/66/diffs?commit_id=f218633aa73ec8ec8004850f10421b94230f1159) |
| ShareableLink | [1cabe7](https://git.cs.dal.ca/courses/2025-winter/csci-5308/group01/-/merge_requests/67/diffs?commit_id=1cabe70ec42cc69a8d156d641429b0531746b285) | [420a2b](https://git.cs.dal.ca/courses/2025-winter/csci-5308/group01/-/merge_requests/67/diffs?commit_id=420a2b46d31776fde653d2049d7148ce266685b2) |
|               | [4930cf](https://git.cs.dal.ca/courses/2025-winter/csci-5308/group01/-/merge_requests/67/diffs?commit_id=4930cf65b97c0cb9988e294bb1801f3ef5d07eb3) | [8d51d3](https://git.cs.dal.ca/courses/2025-winter/csci-5308/group01/-/merge_requests/67/diffs?commit_id=8d51d365b7d3669d436faefdff9c052ee28cf7f0) |
|               | [49d070](https://git.cs.dal.ca/courses/2025-winter/csci-5308/group01/-/merge_requests/67/diffs?commit_id=49d07014098d7c5846191efc175085385e6e762c) | [6ce781](https://git.cs.dal.ca/courses/2025-winter/csci-5308/group01/-/merge_requests/67/diffs?commit_id=6ce7819638bfb3b91c74ee6090e649025ad2f029) |
| Session       | [90682c](https://git.cs.dal.ca/courses/2025-winter/csci-5308/group01/-/merge_requests/74/diffs?commit_id=90682c1ca90aad8d94358b0f75afd9603d3bbd2f) | [8b4aae](https://git.cs.dal.ca/courses/2025-winter/csci-5308/group01/-/merge_requests/74/diffs?commit_id=8b4aae08e03dd7efebb113e5f53776ddf7b74efa) |
| Summary       | [ea345d](https://git.cs.dal.ca/courses/2025-winter/csci-5308/group01/-/merge_requests/76/diffs?commit_id=ea345d410394ce4a9fbbf2f58bade0dcdb2dfce5) | [e1e0a8](https://git.cs.dal.ca/courses/2025-winter/csci-5308/group01/-/merge_requests/76/diffs?commit_id=2f86f697c6b5b813086b26769cfe640a1f2d1945) |

---

## Code Coverage

| Services          | Code Coverage (Class) | Code Coverage (Lines) |
| ----------------- | --------------------- | --------------------- |
| Auth              | 100%                  | 98%                   |
| Document          | 100%                  | 99%                   |
| Messages          | 100%                  | 100%                  |
| PluginRedirection | 100%                  | 100%                  |
| Session           | 100%                  | 100%                  |
| ShareableLink     | 100%                  | 97%                   |
| Summary           | 100%                  | 100%                  |

---

## Contributors

- B00863868: Akshat Gulati - (ak922007@dal.ca)
- B01025608: Nakul Patel - (nk873706@dal.ca)
- B00988337: Het Patel - (ht526322@dal.ca)
- B01033206: Dhruva Patil - (dh602843@dal.ca)
- B01031699: Awwal Algabe - (aw5565230@dal.ca)

[Individual Contribution](https://docs.google.com/spreadsheets/d/1yqmBvZs3LjlS_9DFFfQ_KsvACKDGzN9m59G6mXUygIQ/edit?gid=0#gid=0)

---

## User Stories

### Story 1: Instant Code Summaries

Sarah, a junior developer, is reading a blog post about optimizing JavaScript performance. She encounters an unfamiliar code snippet:

```javascript
function debounce(func, wait) {
  let timeout;
  return function executedFunction(...args) {
    clearTimeout(timeout);
    timeout = setTimeout(() => func(...args), wait);
  };
}
```

Sarah highlights the code using the Chrome extension.
She clicks "Summarize" and gets a popup:

> "This code limits how often a function runs, waiting a set time before executing it—great for rate-limiting events."

With this quick explanation, she applies it to her project to optimize an input field’s search functionality.

---

### Story 2: Contextual Definitions

Mark, a product manager with limited technical knowledge, is reviewing a design document online. He stumbles across the term **"REST API"** and isn’t sure what it means.

- He highlights **"REST API"** with the Chrome extension.
- He selects "Define" and sees:
  > "A set of rules for web services to communicate over HTTP using methods like GET and POST."

Now understanding the term, Mark confidently discusses integration needs with his development team.

---

### Story 3: Save and Download Responses

Emma, a software engineer, frequently uses the Chrome extension to summarize code snippets and define technical terms.

- She highlights a piece of JavaScript code and clicks "Summarize."
- The tool generates a clear explanation.
- Emma wants to keep a record of the summary for future reference.
- She clicks "Save Response," which stores the result in the extension's history.
- She also has the option to download the response as a `.docx` file for documentation purposes.

This feature helps Emma organize her technical notes and easily share insights with her team.

---

### Story 4: Automated Documentation Generator

Priya, a freelance developer, finishes a Node.js module for a client:

```javascript
class UserService {
  constructor(db) {
    this.db = db;
  }

  async getUser(id) {
    return await this.db.findUser(id);
  }
}
module.exports = UserService;
```

She uploads the file to the website and clicks **"Generate Documentation."**

- The tool creates a detailed Google Doc with class and method descriptions.
- Priya shares the link with her client, who approves the work without needing further clarification.

---

### Story 5: Lightweight Extension with Web Integration

Jake, a full-stack developer, is browsing a tutorial site. He selects a small CSS snippet:

```css
.container {
  display: flex;
  justify-content: center;
}
```

- He uses the Chrome extension and gets:
  > "This CSS centers content horizontally using flexbox."

Later, he tries a **500-line JavaScript file**, and the extension prompts him to use the website.

- Jake uploads it there, receives a full analysis, and appreciates the seamless switch between tools.

---

## Acknowledgments

Special thanks to:

- Dalhousie University, Halifax, Nova Scotia
- CSCI 5308 Course Staff - [Prof. Tushar Sharma, TA Saurabh Rajput]
- Client Team - Group06

---

## THANK YOU FOR CHOOSING CODELENS!

©2025 CSCI_5308_GROUP1. All rights reserved.
