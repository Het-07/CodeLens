import { EMAIL_REGEX, STRONG_PASSWORD_REGEX } from "../constants/constants";

export const validateEmail = (email: string): string => {
  if (!email.trim()) {
    return "Email is required";
  } else if (!EMAIL_REGEX.test(email)) {
    return "Invalid email format";
  }
  return "";
};

export const validatePassword = (password: string): string => {
  if (!password.trim()) {
    return "Password is required";
  } else if (!STRONG_PASSWORD_REGEX.test(password)) {
    return "Password must be at least 8 characters long, include one uppercase letter, one number, and one special character";
  }
  return "";
};

export const validateConfirmPassword = (
  password: string,
  confirmPassword: string
): string => {
  if (!confirmPassword.trim()) {
    return "Confirm password is required";
  } else if (confirmPassword !== password) {
    return "Passwords do not match";
  }
  return "";
};

export const validateFirstName = (firstName: string): string => {
  if (!firstName.trim()) {
    return "First Name is required";
  }
  return "";
};

export const validateLastName = (lastName: string): string => {
  if (!lastName.trim()) {
    return "Last Name is required";
  }
  return "";
};
