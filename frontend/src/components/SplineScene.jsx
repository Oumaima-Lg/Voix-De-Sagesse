import { lazy, Suspense } from 'react';

// The Spline 3D runtime is large: it is only downloaded when a scene is actually displayed.
const Spline = lazy(() => import('@splinetool/react-spline'));

const SplineScene = ({ scene }) => (
  <Suspense fallback={null}>
    <Spline scene={scene} />
  </Suspense>
);

export default SplineScene;
