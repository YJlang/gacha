import React from "react";
import { Navigate } from "react-router-dom";

const PrivateRoute = ({ element: Element, ...rest }) => {
  const token = localStorage.getItem("jwt_token");

  if (token) {
    return <Element {...rest} />;
  }

  return <Navigate to="/auth" replace />;
};

export default PrivateRoute;
