import React from 'react';
import { useState } from 'react';
import { registerUser } from '../../api/authService';
import { signupValidation } from '../../utils/formValidation';
import { TextInput } from '@mantine/core';
import { useNavigate } from 'react-router-dom';
import { errorNotification, successNotification } from '../../utils/notifications';
import './RegisterForm.css';

const form = {
    nom: "",
    prenom: "",
    email: "",
    motdepasse: "",
    confirmerMotdepasse: "",
    accountType: "USER"
}

const RegisterForm = () => {

    const [data, setData] = useState(form);
    const [formError, setFormError] = useState(form);
    const navigate = useNavigate();

    const handleChange = (e) => {
        const { name, value } = e.target;
        const newData = { ...data, [name]: value };
        setData(newData);

        const newErrors = { ...formError, [name]: signupValidation(name, value) };

        if (name === "motdepasse" && newData.confirmerMotdepasse !== "") {
            newErrors.confirmerMotdepasse =
                newData.confirmerMotdepasse !== value ?
                    "Les mots de passe ne correspondent pas." : "";
        }

        if (name === "confirmerMotdepasse") {
            newErrors.confirmerMotdepasse =
                newData.motdepasse !== value ?
                    "Les mots de passe ne correspondent pas." : "";
        }

        setFormError(newErrors);
    };

    const handleSubmit = (e) => {

        let valid = true;
        let newFormError = {};

        for (let key in data) {
            if (key === "accountType") continue;

            if (key === "confirmerMotdepasse") {
                if (data[key] !== data["motdepasse"]) {
                    newFormError[key] = "Les mots de passe ne correspondent pas.";
                }
            } else {
                newFormError[key] = signupValidation(key, data[key]);
            }

            if (newFormError[key]) valid = false;
        }

        setFormError(newFormError);

        e.preventDefault();

        if (valid === true) {
            registerUser(data).then(() => {
                setData(form);
                successNotification('Succès', 'Inscription réussie !');
                
                setTimeout( () => {
                    navigate("/auth/login")
                }, 2000 )

            }).catch((err) => {
                errorNotification("Échec de l'inscription", err.response
                    ? err.response.data?.errorMessage || "Erreur lors de l'inscription."
                    : 'Impossible de contacter le serveur. Réessayez plus tard.');
                
            });
        }
    }

    return (
        <div className='register-form'>
            <div className="mx-auto px-4">
                <div className='card w-[min(100vw_-_2rem,26rem)] mx-auto border-4 border-image-gradient sm:px-8 px-4 py-4 flex flex-col gap-4'>
                    <h4 className="text-gradient sm:text-3xl text-2xl text-center pt-2">Créer un compte</h4>
                    <form className='flex flex-col gap-3'>
                        <div className='grid sm:grid-cols-2 gap-3'>
                            <TextInput error={formError.prenom} size='md' placeholder="Prénom" value={data.prenom} onChange={handleChange} name="prenom" />
                            <TextInput
                                error={formError.nom}
                                size='md'
                                placeholder="Nom de famille"
                                value={data.nom}
                                onChange={handleChange}
                                name="nom"
                            />
                        </div>
                        <TextInput
                            error={formError.email}
                            size='md'
                            placeholder="Email"
                            value={data.email}
                            onChange={handleChange}
                            name="email"
                        />
                        <TextInput
                            error={formError.motdepasse}
                            size='md'
                            placeholder="Mot de passe"
                            type="password"
                            value={data.motdepasse}
                            onChange={handleChange}
                            name="motdepasse"
                        />
                        <TextInput
                            error={formError.confirmerMotdepasse}
                            size='md'
                            placeholder="Confirmez le mot de passe"
                            type="password"
                            value={data.confirmerMotdepasse}
                            onChange={handleChange}
                            name="confirmerMotdepasse"
                        />
                        <div>
                            <button className="btn btn-border-gradient text-amber-50! mb-3!" onClick={handleSubmit} >S'inscrire</button>
                            <div className='h-0.5 mb-3 bg-gradient-to-br from-[#4C3163] to-[#A09F87]' />
                            <span className="block mb-3! border-b-1 max-w-max mx-auto cursor-pointer" onClick={ () => {navigate("/auth/login"); setFormError(form); setData(form)} }>Vous avez déjà un compte ?</span>
                        </div>

                    </form>
                </div>

            </div>
        </div>
    );
}

export default RegisterForm;
