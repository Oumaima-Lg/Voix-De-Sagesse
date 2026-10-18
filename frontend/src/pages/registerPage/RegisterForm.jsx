import React from 'react';
import { useEffect, useRef, useState } from 'react';
import { registerUser, verifyRegistration } from '../../api/authService';
import { getApiErrorMessage } from '../../api/apiError';
import { signupValidation } from '../../utils/formValidation';
import { PinInput, TextInput, useMatches } from '@mantine/core';
import { useNavigate } from 'react-router-dom';
import { errorNotification, successNotification } from '../../utils/notifications';
import './RegisterForm.css';

// Same delay as the server before a new code can be requested.
const RESEND_DELAY_SECONDS = 60;

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
    // 'form', then 'code' once the verification code has been emailed.
    const [step, setStep] = useState('form');
    const [sending, setSending] = useState(false);
    const [code, setCode] = useState('');
    const [codeError, setCodeError] = useState(false);
    const [verifying, setVerifying] = useState(false);
    const [cooldown, setCooldown] = useState(0);
    const pinRef = useRef(null);
    // Six boxes must fit in the card on a 320px wide phone.
    const pinSize = useMatches({ base: 'sm', xs: 'md' });
    const pinGap = useMatches({ base: 6, xs: 'md' });

    useEffect(() => {
        if (cooldown <= 0) return;
        const timer = setTimeout(() => setCooldown((s) => s - 1), 1000);
        return () => clearTimeout(timer);
    }, [cooldown]);

    // After a refused code the boxes are emptied: put the cursor back in the first one
    // (PinInput gives its ref to the first box).
    useEffect(() => {
        if (codeError) pinRef.current?.focus();
    }, [codeError]);

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

        if (valid === true && !sending) sendCode();
    }

    const sendCode = () => {
        setSending(true);
        registerUser(data).then((res) => {
            successNotification('Code envoyé', res.message);
            setStep('code');
            setCode('');
            setCodeError(false);
            setCooldown(RESEND_DELAY_SECONDS);
        }).catch((err) => {
            errorNotification("Échec de l'inscription", getApiErrorMessage(err, "Erreur lors de l'inscription."));
        }).finally(() => setSending(false));
    }

    const handleVerify = (value) => {
        if (verifying) return;
        setVerifying(true);
        verifyRegistration(data.email, value).then(() => {
            successNotification('Compte créé', 'Vous pouvez maintenant vous connecter.');
            setTimeout(() => navigate("/auth/login"), 2000);
        }).catch((err) => {
            setVerifying(false);
            setCode('');
            setCodeError(true);
            errorNotification('Code refusé', getApiErrorMessage(err));
        });
    }

    return (
        <div className='register-form'>
            <div className="mx-auto px-4">
                <div className='card w-[min(100vw_-_2rem,26rem)] mx-auto border-4 border-image-gradient sm:px-8 px-4 py-4 flex flex-col gap-4'>
                    {step === 'code' ? (
                        <>
                            <h4 className="text-gradient sm:text-3xl text-2xl text-center pt-2">Vérifiez votre email</h4>
                            <p className='text-sm text-white/75'>
                                Saisissez le code à 6 chiffres envoyé à <span className='text-white font-medium break-all'>{data.email}</span>. Il est valable 10 minutes.
                            </p>
                            <div className='flex flex-col gap-3'>
                                <PinInput
                                    ref={pinRef}
                                    className='mx-auto'
                                    length={6}
                                    type="number"
                                    size={pinSize}
                                    gap={pinGap}
                                    value={code}
                                    onChange={(value) => { setCode(value); if (value) setCodeError(false); }}
                                    onComplete={handleVerify}
                                    error={codeError}
                                    disabled={verifying}
                                    autoFocus
                                    ariaLabel="Code de vérification"
                                />
                                <div>
                                    <button type="button" className="btn btn-border-gradient text-amber-50! mb-3!" disabled={code.length < 6 || verifying} onClick={() => handleVerify(code)}>
                                        {verifying ? 'Vérification...' : 'Valider'}
                                    </button>
                                    <div className='h-0.5 mb-3 bg-gradient-to-br from-[#4C3163] to-[#A09F87]' />
                                    <div className='flex flex-wrap justify-center gap-x-6 gap-y-2 mb-3'>
                                        <button type="button" className={`border-b-1 ${cooldown > 0 || sending ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer'}`} disabled={cooldown > 0 || sending} onClick={sendCode}>
                                            {cooldown > 0 ? `Renvoyer le code (${cooldown} s)` : 'Renvoyer le code'}
                                        </button>
                                        <button type="button" className='border-b-1 cursor-pointer' disabled={verifying} onClick={() => setStep('form')}>
                                            Modifier mes informations
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </>
                    ) : (
                        <>
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
                                    <button className="btn btn-border-gradient text-amber-50! mb-3!" onClick={handleSubmit} disabled={sending}>{sending ? 'Envoi du code...' : "S'inscrire"}</button>
                                    <div className='h-0.5 mb-3 bg-gradient-to-br from-[#4C3163] to-[#A09F87]' />
                                    <span className="block mb-3! border-b-1 max-w-max mx-auto cursor-pointer" onClick={ () => {navigate("/auth/login"); setFormError(form); setData(form)} }>Vous avez déjà un compte ?</span>
                                </div>

                            </form>
                        </>
                    )}
                </div>

            </div>
        </div>
    );
}

export default RegisterForm;
