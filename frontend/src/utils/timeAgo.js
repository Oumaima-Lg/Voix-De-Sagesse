const rtf = new Intl.RelativeTimeFormat("fr", { numeric: "auto" });

export const timeAgo = (date, now = new Date()) => {
  const seconds = Math.round((new Date(date) - now) / 1000);
  if (seconds > -60) return "à l'instant";
  const minutes = Math.round(seconds / 60);
  if (minutes > -60) return rtf.format(minutes, "minute");
  const hours = Math.round(minutes / 60);
  if (hours > -24) return rtf.format(hours, "hour");
  const days = Math.round(hours / 24);
  if (days > -7) return rtf.format(days, "day");
  return new Date(date).toLocaleDateString("fr-FR");
};
