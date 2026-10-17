import React, { useState } from 'react'
import { Button, Modal, PasswordInput, PinInput, TextInput } from '@mantine/core';
import { useInterval } from '@mantine/hooks';
import { changePass, sendOtp, verifyOtp } from '../../services/AuthService';
import { signupValidation } from '../../services/FormValidation';
import { errorNotification, successNotification } from '../../services/NotificationService';
import { getApiErrorMessage } from '../../services/apiError';

const ResetPassword = (props) => {
    const [email, setEmail] = useState("");
    const [motdepasse, setMotdepasse] = useState("");
    const [passErr, setPassErr] = useState("");
    const [otpSent, setOtpSent] = useState(false);
    const [otpSending, setOtpSending] = useState(false);
    const [verified, setVerified] = useState(false);
    const [resetToken, setResetToken] = useState("");
    const [resendLoader, setResendLoader] = useState(false);
    const [seconds, setSeconds] = useState(60);
    const interval = useInterval(() => {
        if (seconds === 0) {
            setResendLoader(false);
            setSeconds(60);
            interval.stop();
        } else
            setSeconds((s) => s - 1)
    }, 1000);

    const handleSendOtp = () => {
        setOtpSending(true);
        sendOtp(email).then((res) => {
            successNotification("Code envoyé", res.message);
            setOtpSent(true);
            setOtpSending(false);
            setResendLoader(true);
            interval.start();
        }).catch((err) => {
            setOtpSending(false);
            errorNotification("Échec de l'envoi", getApiErrorMessage(err));
        })
    }

    const handleVerifyOtp = (otp) => {
        verifyOtp(email, otp).then((res) => {
            setResetToken(res.resetToken);
            successNotification("Code vérifié", "Choisissez un nouveau mot de passe.");
            setVerified(true);
        }).catch((err) => {
            errorNotification("Code refusé", getApiErrorMessage(err));
        })
    }

    const resendOtp = () => {
        if (resendLoader) return;
        handleSendOtp();
    }

    const changeEmail = () => {
        setResendLoader(false);
        setOtpSent(false);
        setSeconds(60);
        setVerified(false);
        setResetToken("");
        interval.stop();
    }

    const handleResetPassword = () => {
        const error = signupValidation("motdepasse", motdepasse);
        if (error) {
            setPassErr(error);
            return;
        }
        changePass(email, resetToken, motdepasse).then(() => {
            successNotification("Mot de passe modifié", "Connectez-vous avec votre nouveau mot de passe.");
            props.close();
            setEmail("");
            setMotdepasse("");
            setPassErr("");
            setOtpSent(false);
            setOtpSending(false);
            setVerified(false);
            setResetToken("");
            setResendLoader(false);
            setSeconds(60);
            interval.stop();

        }).catch((err) => {
            errorNotification("Échec de la réinitialisation", getApiErrorMessage(err));
        })

    }



    return (
        <Modal opened={props.opened} onClose={props.close} title="Réinitialiser le mot de passe">
            <div className='flex flex-col gap-6'>
                <TextInput
                    size='lg'
                    placeholder="Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    name="email"
                    rightSectionWidth="xl"
                    rightSection={
                        <Button loading={otpSending} className="text-amber-50 rounded-md h-3/4 mr-2 px-2 cursor-pointer " disabled={email === "" || otpSent} onClick={handleSendOtp}>Envoyer</Button>
                    }
                />

                {otpSent && <PinInput length={6} onComplete={handleVerifyOtp} size='md' gap="lg" className='mx-auto' type="number" />}

                {otpSent && !verified &&
                    <div className='flex gap-2'>
                        <Button fullWidth loading={otpSending && !otpSent} autoContrast variant='light' onClick={resendOtp}>{resendLoader ? seconds : "Renvoyer"}</Button>
                        <Button fullWidth autoContrast variant='filled' onClick={changeEmail}>Changer l'email</Button>

                    </div>
                }

                {verified && <PasswordInput
                    error={passErr}
                    size='lg'
                    placeholder="Mot de passe"
                    value={motdepasse}
                    onChange={(e) => { setMotdepasse(e.target.value); setPassErr(signupValidation("motdepasse", e.target.value)); }}
                    name="motdepasse"
                />}

                {verified &&
                    <Button variant='filled' onClick={handleResetPassword} >Modifier le mot de passe</Button>
                }

            </div>
        </Modal>

    )
}

export default ResetPassword
