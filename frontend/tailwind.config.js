module.exports = {
  content: [
    "./src/**/*.{html,js,jsx,ts,tsx}", // Vérifie que tes fichiers React sont bien inclus ici
  ],
  theme: {
    fontFamily: {
      primary: 'Orbitron',
      secondary: 'Rajdhani',
      tertiary: 'Aldrich',
    },
    extend: {
      fontFamily: {
        charm: ['"Charm"', 'cursive'], 
      },
    },
    // container: {
    //   padding: {
    //     DEFAULT: '15px',
    //   },
    // },
  },
  plugins: [],
}

// /** @type {import('tailwindcss').Config} */
// module.exports = {
//   content: ["./src/**/*.{html,js,ts,jsx,tsx}"],
//   theme: {
//       extend: {},
//   },
//   plugins: [],
// };
