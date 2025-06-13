export interface ICreateMessage {
    userId: string,
    userPrompt: string,
    sessionId: string,
    isCodeModelEnabled: boolean
}

export interface ICreateSession  {
    userId: string, 
    userPrompt: string
}

export interface ICreateUserMessage {
    userId: string,
    message: string,
    sessionId: string,
    isAIGenerated: boolean
}