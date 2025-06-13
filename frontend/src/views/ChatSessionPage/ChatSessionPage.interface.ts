
export interface IMessage {
    id: string,
    message: string,
    isAIGenerated: boolean,
    createdAt: Date
}


export interface ICreateMessage {
    userId: string,
    sessionId: string,
    isAIGenerated: boolean,
    message: string
}

export interface IGenerateResponse {
    userId: string,
    sessionId: string,
    userPrompt: string,
    isCodeModelEnabled: boolean
}

export interface ISession {
    sessionName: string,
    userId: string,
    id: string,
    createdAt: string,
    updatedAt: string
}

export interface IChatSessionInitialState {
    sessionLoadingStates: Map<string, boolean>, // Use a Map
    selectedSession: ISession,
    isCodeModeEnabled: boolean,
    allSessions: ISession[]
}