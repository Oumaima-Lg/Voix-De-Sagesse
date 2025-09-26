

import { notifications } from '@mantine/notifications'
import { IconCheck, IconX } from '@tabler/icons-react'
import React from 'react'

const successNotification = (title, message) => {
    notifications.show({
        title: title,
        message: message,
        withCloseButton: true,
        className: "!border-green-500 !border-1",
        icon: <IconCheck style={{ width: "90%", height: "90%" }} />,
        color: "teal"
    })
}


const errorNotification = (title, message) => {
    notifications.show({
        title: title,
        message: message,
        withCloseButton: true,
        className: "!border-red-500 !border-1",
        icon: <IconX style={{ width: "90%", height: "90%" }} />,
        color: "red"

    })
}

export {successNotification, errorNotification};
