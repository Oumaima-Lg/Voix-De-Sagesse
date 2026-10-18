import { render } from '@testing-library/react'
import { MantineProvider } from '@mantine/core'
import { configureStore } from '@reduxjs/toolkit'
import { Provider } from 'react-redux'
import { MemoryRouter } from 'react-router-dom'
import userReducer from '../store/userSlice'
import jwtReducer from '../store/jwtSlice'

export const makeStore = (user = null) =>
  configureStore({ reducer: { user: userReducer, jwt: jwtReducer }, preloadedState: { user, jwt: null } })

// Renders `ui` with the same providers as the app, at the given URL.
export const renderWithProviders = (ui, { route = '/', user = null } = {}) => {
  const store = makeStore(user)
  return {
    store,
    ...render(
      <Provider store={store}>
        <MantineProvider>
          <MemoryRouter initialEntries={[route]}>{ui}</MemoryRouter>
        </MantineProvider>
      </Provider>,
    ),
  }
}
