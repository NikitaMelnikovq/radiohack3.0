import type { Config } from "tailwindcss";

export default {
  content: ["./index.html", "./src/**/*.{ts,tsx}"],
  theme: {
    extend: {
      colors: {
        t: {
          yellow: "#FFDD2D",
          "yellow-hover": "#FCC521",
          "yellow-active": "#FAB619",
          black: "#0B0B0C",
          surface: "#151518",
          "surface-2": "#202024",
          gray: "#8A8A8E",
          border: "rgba(255,255,255,0.08)",
          white: "#FFFFFF",
          success: "#2ECC71",
          warning: "#FFB020",
          danger: "#FF5A5F",
        },
      },
      borderRadius: {
        "4xl": "2rem",
      },
      boxShadow: {
        soft: "0 24px 80px rgba(0,0,0,0.28)",
        glow: "0 0 40px rgba(255,221,45,0.16)",
      },
      fontFamily: {
        sans: ["Inter", "system-ui", "-apple-system", "BlinkMacSystemFont", "\"Segoe UI\"", "sans-serif"],
      },
    },
  },
  plugins: [
    ({ addVariant }) => {
      addVariant("light", ".light &");
    },
  ],
} satisfies Config;
