// import "bootstrap/dist/css/bootstrap.min.css";
import "./index.scss";
import { render } from "preact";
import { App } from "./app.tsx";
import { StrictMode } from "react";

render(
  <StrictMode>
    <App />
  </StrictMode>,
  document.getElementById("app")!,
);
