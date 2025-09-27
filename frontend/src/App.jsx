
import '@mantine/notifications/styles.css';

import '@mantine/core/styles.css';
import { MantineProvider } from '@mantine/core';
import { Notifications } from '@mantine/notifications';
import '@mantine/carousel/styles.css';
import Store from './Store'
import { Provider } from 'react-redux'
import AppRoutes from './routes/AppRoutes';
import { BrowserRouter } from 'react-router-dom';

const App = () => {

  return (
    <Provider store={Store}>
      < MantineProvider defaultColorScheme="dark" >
        <Notifications position="top-center" zIndex={1000} />
        <BrowserRouter>
          <AppRoutes />
        </BrowserRouter>
      </MantineProvider >
    </Provider>


  )
}

export default App

