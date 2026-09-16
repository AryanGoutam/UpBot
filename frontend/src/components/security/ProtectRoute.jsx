import { Navigate } from "react-router-dom";
import { toast } from "react-toastify";
import { useEffect } from "react";

const ProtectedRoute = ({ children }) => {
  const token = localStorage.getItem("token");

  useEffect(() => {
    if (!token) {
      toast.error("You need to login first");
    }
  }, [token]);

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  return children;
};

export default ProtectedRoute;