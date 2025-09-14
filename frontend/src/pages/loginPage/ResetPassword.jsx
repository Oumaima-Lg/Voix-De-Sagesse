import React, { useState } from 'react'
import { Button, Modal, PasswordInput, PinInput, TextInput } from '@mantine/core';
import { useInterval } from '@mantine/hooks';
import { changePass, sendOtp, verifyOtp } from '../../services/AuthService';
import { signupValidation } from '../../services/FormValidation';
import { errorNotification, successNotification } from '../../services/NotificationService';

const ResetPassword = (props) => {
    const [email, setEmail] = useState("");
    const [motdepasse, setMotdepasse] = useState("");
    const [passErr, setPassErr] = useState("");
    const [otpSent, setOtpSent] = useState(false);
    const [otpSending, setOtpSending] = useState(false);
    const [verified, setVerified] = useState(false);
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
            console.log(res);
            successNotification("OTP sent succ", "Enter OTP");
            setOtpSent(true);
            setOtpSending(false);
            setResendLoader(true);
            interval.start();
        }).catch((err) => {
            console.log(err);
            setOtpSending(false);
            errorNotification("Failed OTP", err.response.data.errorMessage);
        })
    }

    const handleVerifyOtp = (otp) => {
        verifyOtp(email, otp).then((res) => {
            console.log(res);
            successNotification("OTP verified", "Enter new password.");
            setVerified(true);
        }).catch((err) => {
            console.log(err);
            errorNotification("OTP ver failed", err.response.data.errorMessage);
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
        interval.stop();
    }

    const handleResetPassword = () => {
        console.log(motdepasse);
        changePass(email, motdepasse).then((res) => {
            console.log(res);
            successNotification("Password changed", "Login with new password.");
            props.close();
            setEmail("");
            setMotdepasse("");
            setPassErr("");
            setOtpSent(false);
            setOtpSending(false);
            setVerified(false);
            setResendLoader(false);
            setSeconds(60);
            interval.stop();

        }).catch((err) => {
            console.log(err);
            errorNotification("Password reset failed", err.response.data.errorMessage);
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
                        <Button loading={otpSending} className="text-amber-50 rounded-md h-3/4 mr-2 px-2 cursor-pointer " disabled={email === "" || otpSent} onClick={handleSendOtp}>CONNEXION</Button>
                    }
                />

                {otpSent && <PinInput length={6} onComplete={handleVerifyOtp} size='md' gap="lg" className='mx-auto' type="number" />}

                {otpSent && !verified &&
                    <div className='flex gap-2'>
                        <Button fullWidth loading={otpSending && !otpSent} autoContrast variant='light' onClick={resendOtp}>{resendLoader ? seconds : "Resend"}</Button>
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
