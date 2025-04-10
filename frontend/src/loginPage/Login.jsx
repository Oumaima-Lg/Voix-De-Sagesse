import React from 'react'
import LoginForm from './LoginForm'
import RegisterForm from './RegisterForm'

const Login = () => {
  return (
    <div className='text-white flex items-center justify-center pt-20 '>
      <div className=''>
        {/* <RegisterForm /> */}
        <LoginForm />
      </div>
    </div>
  )
}

export default Login
