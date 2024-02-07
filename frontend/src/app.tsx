import { createBrowserRouter, RouterProvider } from "react-router-dom";
import JoinRoomPage from "./pages/JoinRoomPage.tsx";

export function App() {
  const router = createBrowserRouter([
    {
      path: "/",
      element: <JoinRoomPage />,
    },
  ]);

  return (
    <div className={"center-window"}>
      <RouterProvider router={router} />
    </div>
  );
}
