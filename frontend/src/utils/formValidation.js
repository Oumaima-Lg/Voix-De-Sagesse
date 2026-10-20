const EMAIL_REGEX = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
const PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,15}$/;

const signupValidation = (name, value) => {
  switch (name) {
    case "nom":
      if (value.trim().length === 0) return "Le nom est obligatoire.";
      return "";

    case "prenom":
      if (value.trim().length === 0) return "Le prénom est obligatoire.";
      return "";

    case "email":
      if (value.trim().length === 0) return "L'email est obligatoire.";
      if (!EMAIL_REGEX.test(value)) return "L'email n'est pas valide.";
      return "";

    case "motdepasse":
      if (value.length === 0) return "Le mot de passe est obligatoire.";
      if (!PASSWORD_REGEX.test(value)) {
        return "8 à 15 caractères, avec une majuscule, une minuscule, un chiffre et un caractère spécial (@$!%*?&).";
      }
      return "";

    default:
      return "";
  }
};


const loginValidation = (name, value) => {
  switch (name) {

    case "email":
      if (value.trim().length === 0) return "L'email est obligatoire.";
      if (!EMAIL_REGEX.test(value)) return "L'email n'est pas valide.";
      return "";

    case "password":
      if (value.length === 0) return "Le mot de passe est obligatoire.";
      return "";

    default:
      return "";
  }
};

export {signupValidation, loginValidation};
