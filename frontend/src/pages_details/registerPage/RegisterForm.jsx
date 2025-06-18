import React from 'react';
import styled from 'styled-components';
import { useState } from 'react';
import { registerUser } from '../../services/UserService';
import { signupValidation } from '../../services/FormValidation';
import { TextInput } from '@mantine/core';

const form = {
    nom: "",
    prenom: "",
    email: "",
    motdepasse: "",
    confirmerMotdepasse: "",
    accountType: "ADMIN"
}

const RegisterForm = () => {

    const [data, setData] = useState(form);
    const [formError, setFormError] = useState(form);

    const handleChange = (e) => {
        console.log(e.target);
        let name = e.target.name, value = e.target.value;
        setData({ ...data, [name]: value });
        setFormError({ ...formError, [name]: signupValidation(name, value) });
    }

    const handleSubmit = (e) => {
        e.preventDefault(); // il est important de l'ajouter pour empêcher le comportement par défaut du formulaire

        registerUser(data).then((res) => {
            console.log(res);
            alert('Inscription réussie !');

        }).catch((err) => {
            console.log(err);
        });

    }

    return (
        <StyledWrapper>
            <div className="mx-auto  p-14">
                <div className='card mx-auto border-4 border-image-gradient sm:px-9 sm:py-4 px-4 py-2 flex flex-col md:gap-10 sm:gap-8 gap-6'>
                    <h4 className="text-gradient lg:text-4xl sm:text-3xl text-xl text-center lg:px-20 px-10 pt-2">Créer un compte</h4>
                    <form className='flex flex-col md:gap-4 gap-2'>
                        {/* <input autoComplete="off" value={data.prenom} onChange={handleChange} id="prenom" name="prenom" type="text" placeholder="Prénom" className="bg-none border-0 outline-0 text-sm sm:text-lg text-[#d3d3d3] lg:w-sm w-auto " /> */}
                        <TextInput
                            error={formError.prenom}
                            size='lg'
                            placeholder="Prénom"
                            value={data.prenom}
                            onChange={handleChange}
                            name="prenom"
                        />
                        <TextInput
                            error={formError.nom}
                            size='lg'
                            placeholder="Nom de famille"
                            value={data.nom}
                            onChange={handleChange}
                            name="nom"
                        /> <TextInput
                            error={formError.email}
                            size='lg'
                            placeholder="Email"
                            value={data.email}
                            onChange={handleChange}
                            name="email"
                        />
                        <TextInput
                            error={formError.motdepasse}
                            size='lg'
                            placeholder="Mot de passe"
                            value={data.motdepasse}
                            onChange={handleChange}
                            name="motdepasse"
                        />

                        <TextInput
                            error={formError.confirmerMotdepasse}
                            size='lg'
                            placeholder="Confirmez le mot de passe"
                            value={data.confirmerMotdepasse}
                            onChange={handleChange}
                            name="confirmerMotdepasse"
                            whithAsterik
                        />

                        <div>
                            <button className="btn btn-border-gradient text-amber-50! mb-6!" onClick={handleSubmit} >S'inscrire</button>
                            <div className='h-0.5 mb-6 bg-gradient-to-br from-[#4C3163] to-[#A09F87]' />
                            <a href="/auth/login" className="block mb-6! border-b-1 max-w-max mx-auto">Vous avez déja un compte ?</a>
                        </div>

                    </form>
                </div>

            </div>
        </StyledWrapper>
    );
}

const StyledWrapper = styled.div`
  .card {
    box-shadow: 
    8px 8px 20px -5px rgba(76, 49, 99, 0.4),
    -5px -5px 15px rgba(160, 159, 135, 0.2),
    inset 2px 2px 5px rgba(255, 255, 255, 0.1);
   text-align: center;
  }


  .input-icon {
   height: 1em;
   width: 1em;
//    fill: #ffeba7;
  }


  /*Buttons*/
  .btn {
   margin: 1rem;
   border: none;
   border-radius: 4px;
   font-weight: bold;
   font-size: .8em;
   text-transform: uppercase;
   padding: 0.6em 1.2em;
//    background-color: #ffeba7;
//    color: #5e6681;
   box-shadow: 0 8px 24px 0 rgb(255 235 167 / 20%);
   transition: all .3s ease-in-out;
  }

  .btn-link {
//    color: #f5f5f5;
   display: block;
   font-size: .75em;
   transition: color .3s ease-out;
  }

  /*Hover & focus*/
  input:focus::placeholder {
   opacity: 0;
   transition: opacity .3s;
  }

  .btn:hover {
//    background-color: #5e6681;
//    color: #ffeba7;
   box-shadow: 0 8px 24px 0 rgb(16 39 112 / 20%);
  }

  .btn-link:hover {
//    color: #ffeba7;
  }`;

export default RegisterForm;
