export const validateEmail = (email: string): string => {
    if (!email.trim().length) {
      return "Email is required";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      return "Invalid email format";
    }
    return "";
  };
  
  export const validatePassword = (password: string): string => {
    if (!/^(?=.*[0-9])(?=.*[@#$%^&+=])(?=.*[A-Z]).{8,}$/.test(password) ) {
      return "Password must be at least 8 characters long and include one uppercase letter, one number and one special character";
    } 
    return "";
  };
  
  export const validateConfirmPassword = (password: string, confirmPassword: string): string => {
    if (!confirmPassword.trim().length) {
      return "Confirm password is required"; // Added empty check
    } else if (confirmPassword !== password) {
      return "Passwords do not match";
    }
    return "";
  };
  export const validateName = (name: string, label: string): string => {
    if (!name.trim().length) {
      return `${label} is required`;
    }
    return "";
  };

  

