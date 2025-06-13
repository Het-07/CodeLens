export interface ISession {
    sessionName: string,
    userId: string,
    id: string,
    createdAt: string,
    updatedAt: string
}

export interface IError {
    body: IErrorBody
}

export interface IErrorBody {
    message: string,
    query_params: object,
    exception_type: string,
    method: string
}

export interface ICreateSession {
    userId: string,
    sessionName: string
}

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

export interface IUpdateSession {
    userId: string,
    sessionId: string,
    sessionName: string
}

export interface IGenerateResponse {
    userId: string,
    sessionId: string,
    userPrompt: string
}