import { createBrowserRouter, RouterProvider } from "react-router-dom";
import HomePage from "./pages/HomePage.tsx";
import RoomPage from "./pages/RoomPage";

const BASE_URL = import.meta.env.VITE_HTML_BASE_URL

export function App() {
  const router = createBrowserRouter([
    {
      path: `/`,
      element: <HomePage />,
    },
    {
      path: `/room`,
      element: <RoomPage />,
    },
  ], {
    basename: BASE_URL
  });

  return (
    <div className={"center-window"}>
      <RouterProvider router={router} />
    </div>
  );
}
