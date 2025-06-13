// Regular expressions for password validation
export const STRONG_PASSWORD_REGEX =
  /^(?=.*[0-9])(?=.*[@#$%^&+=])(?=.*[A-Z]).{8,}$/;

// Regular expressions for email validation
export const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
