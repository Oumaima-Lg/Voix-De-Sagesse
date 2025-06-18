const signupValidation = (name, value) => {
  switch (name) {
    case "nom":
      if (value.length === 0) return "Name is required.";
      return "";

    case "prenom":
      if (value.length === 0) return "Prenom is required.";
      return "";

    case "email":
      if (value.length === 0) return "Email is required.";
      const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
      if (!emailRegex.test(value)) return "Email is invalid.";
      return "";

    case "motdepasse":
      if (value.length === 0) return "Password is required.";
      const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,15}$/;
      if (!passwordRegex.test(value)) {
        return "Password must be 8-15 characters with an uppercase, a lowercase, a digit, and a special character.";
      }
      return "";

    default:
      return "";
  }
};
export {signupValidation};