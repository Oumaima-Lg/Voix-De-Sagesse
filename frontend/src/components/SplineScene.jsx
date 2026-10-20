import { lazy, Suspense } from 'react';

const Spline = lazy(() => import('@splinetool/react-spline'));

const SplineScene = ({ scene }) => (
  <Suspense fallback={null}>
    <Spline scene={scene} />
  </Suspense>
);

export default SplineScene;
