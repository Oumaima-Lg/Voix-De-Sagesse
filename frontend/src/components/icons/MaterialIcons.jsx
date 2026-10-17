// The three Material icons the app used from @mui/icons-material, with the same paths and the same
// SvgIcon styles (fontSize="inherit"), so they render identically without the MUI dependency.
const svgStyle = {
  userSelect: 'none',
  width: '1em',
  height: '1em',
  display: 'inline-block',
  flexShrink: 0,
  transition: 'fill 200ms cubic-bezier(0.4, 0, 0.2, 1) 0ms',
  fill: 'currentColor',
  fontSize: 'inherit',
};

const MaterialIcon = ({ path }) => (
  <svg viewBox="0 0 24 24" focusable="false" aria-hidden="true" style={svgStyle}>
    <path d={path} />
  </svg>
);

export const PermIdentityIcon = () => (
  <MaterialIcon path="M12 5.9c1.16 0 2.1.94 2.1 2.1s-.94 2.1-2.1 2.1S9.9 9.16 9.9 8s.94-2.1 2.1-2.1m0 9c2.97 0 6.1 1.46 6.1 2.1v1.1H5.9V17c0-.64 3.13-2.1 6.1-2.1M12 4C9.79 4 8 5.79 8 8s1.79 4 4 4 4-1.79 4-4-1.79-4-4-4m0 9c-2.67 0-8 1.34-8 4v3h16v-3c0-2.66-5.33-4-8-4" />
);

export const BookmarkBorderIcon = () => (
  <MaterialIcon path="M17 3H7c-1.1 0-1.99.9-1.99 2L5 21l7-3 7 3V5c0-1.1-.9-2-2-2m0 15-5-2.18L7 18V5h10z" />
);

export const LogoutIcon = () => (
  <MaterialIcon path="m17 7-1.41 1.41L18.17 11H8v2h10.17l-2.58 2.58L17 17l5-5zM4 5h8V3H4c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h8v-2H4z" />
);
