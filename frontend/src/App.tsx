import { PrimeReactProvider } from "primereact/api";
import "primereact/resources/themes/lara-light-blue/theme.css";
import "primereact/resources/primereact.min.css";
import { Router } from "./router/Router";

function App() {
  return (
    <PrimeReactProvider>
      <Router />
    </PrimeReactProvider>
  );
}

export default App;
