import { createBrowserRouter, RouterProvider } from "react-router-dom";
import HomePage from "./pages/HomePage.tsx";
import RoomPage from "./pages/RoomPage.tsx";

export function App() {
  const router = createBrowserRouter([
    {
      path: "/",
      element: <HomePage />,
    },
    {
      path: "/room",
      element: <RoomPage />,
    }
  ]);

  return (
    <div className={"center-window"}>
      <RouterProvider router={router} />
    </div>
  );
}
