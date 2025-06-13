import * as React from 'react'
import { createRoot } from 'react-dom/client';
import './popup.css';
import { PrimeReactProvider } from 'primereact/api';
import { Popup } from './popup';


const App: React.FC<{}> = () => {
    return (
        <PrimeReactProvider>
            <Popup />
        </PrimeReactProvider>
    )
}

const appContainer = document.createElement('div')
document.body.appendChild(appContainer)
const root = createRoot(appContainer);
root.render(<App />)
