import { useCallback, useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"
import { Indicator, Loader, Popover, ScrollArea } from "@mantine/core"
import { Bell, ShieldAlert } from "lucide-react"
import { getNotifications, getUnreadCount, markAllNotificationsRead } from "../../api/notificationService"
import { getApiErrorMessage } from "../../api/apiError"
import { errorNotification } from "../../utils/notifications"
import { timeAgo } from "../../utils/timeAgo"
import { fileUrl } from "../../config"
import profileAvatar from "../../assets/profileAvatar.webp"

const POLL_INTERVAL_MS = 30000

const fullName = (actor) => `${actor.prenom} ${actor.nom}`

const message = (notification) => {
  const { actor, articleExcerpt, commentExcerpt } = notification
  switch (notification.type) {
    case "LIKE":
      return <><b>{fullName(actor)}</b> a aimé votre article « {articleExcerpt} »</>
    case "COMMENT":
      return <><b>{fullName(actor)}</b> a commenté « {articleExcerpt} » : <i>{commentExcerpt}</i></>
    case "SAVE":
      return <><b>{fullName(actor)}</b> a ajouté « {articleExcerpt} » à ses favoris</>
    case "FOLLOW":
      return <><b>{fullName(actor)}</b> vous suit désormais</>
    case "ARTICLE_REMOVED":
      return <>Votre article « {articleExcerpt} » a été supprimé par la modération suite à des signalements.</>
    default:
      return null
  }
}

const target = (notification) => {
  if (notification.type === "FOLLOW") return `/espace/auteur/${notification.actor.id}`
  if (notification.type === "ARTICLE_REMOVED") return null
  return "/espace/mes-publications"
}

const NotificationBell = ({ variant = "icon" }) => {
  const navigate = useNavigate()
  const [count, setCount] = useState(0)
  const [opened, setOpened] = useState(false)
  const [notifications, setNotifications] = useState(null)

  const refreshCount = useCallback(() => {
    getUnreadCount().then(setCount).catch(() => {})
  }, [])

  useEffect(() => {
    refreshCount()
    const timer = setInterval(refreshCount, POLL_INTERVAL_MS)
    window.addEventListener("focus", refreshCount)
    return () => {
      clearInterval(timer)
      window.removeEventListener("focus", refreshCount)
    }
  }, [refreshCount])

  const open = async () => {
    setOpened(true)
    setNotifications(null)
    try {
      setNotifications(await getNotifications())
      if (count > 0) {
        await markAllNotificationsRead()
        setCount(0)
      }
    } catch (err) {
      setNotifications([])
      errorNotification("Erreur", getApiErrorMessage(err, "Impossible de charger les notifications"))
    }
  }

  const follow = (notification) => {
    const to = target(notification)
    if (!to) return
    setOpened(false)
    navigate(to)
  }

  const buttonClass = variant === "tab"
    ? "bg-[#202020] rounded-[8px] p-2 text-white hover:text-[#A09F87]"
    : "p-2 rounded text-[#A09F87] hover:text-white hover:bg-[#4C3163]"

  return (
    <Popover opened={opened} onChange={setOpened} position="bottom-end" width="min(380px, calc(100vw - 24px))" shadow="lg" radius="md" zIndex={1000}>
      <Popover.Target>
        <button
          type="button"
          onClick={() => (opened ? setOpened(false) : open())}
          aria-label={count > 0 ? `Notifications : ${count} nouvelle${count > 1 ? "s" : ""}` : "Notifications"}
          className={`${buttonClass} cursor-pointer transition-colors`}
        >
          <Indicator label={count > 9 ? "9+" : count} disabled={count === 0} color="red" size={18} offset={2}>
            <Bell className="w-6 h-6" />
          </Indicator>
        </button>
      </Popover.Target>
      <Popover.Dropdown className="!bg-[#202020] !border-[#4C3163] !p-0 overflow-hidden">
        <div className="px-4 py-3 border-b border-[#4C3163] text-white font-semibold">Notifications</div>
        {notifications === null ? (
          <div className="flex justify-center py-8"><Loader color="#A09F87" size="sm" /></div>
        ) : notifications.length === 0 ? (
          <p className="px-4 py-8 text-center text-sm text-[#A09F87]">Aucune notification pour le moment.</p>
        ) : (
          <ScrollArea.Autosize mah={420} type="auto">
            <ul>
              {notifications.map((notification) => (
                <li key={notification.id}>
                  <button
                    type="button"
                    onClick={() => follow(notification)}
                    className={`w-full flex gap-3 px-4 py-3 text-left border-b border-[#4C3163]/40 transition-colors ${notification.read ? "" : "bg-[#4C3163]/30"} ${target(notification) ? "hover:bg-[#4C3163]/50 cursor-pointer" : "cursor-default"}`}
                  >
                    {notification.actor ? (
                      <img
                        src={notification.actor.profilePicture ? fileUrl(notification.actor.profilePicture) : profileAvatar}
                        onError={(e) => { e.target.src = profileAvatar }}
                        alt=""
                        className="size-9 shrink-0 rounded-full object-cover"
                      />
                    ) : (
                      <span className="size-9 shrink-0 rounded-full bg-red-900/50 text-red-300 flex items-center justify-center">
                        <ShieldAlert className="w-5 h-5" />
                      </span>
                    )}
                    <span className="min-w-0 flex-1">
                      <span className="block text-sm text-white/90 break-words">{message(notification)}</span>
                      <span className="block text-xs text-[#A09F87] mt-1">{timeAgo(notification.createdAt)}</span>
                    </span>
                    {!notification.read && <span className="mt-1.5 size-2 shrink-0 rounded-full bg-red-500" aria-label="Non lue" />}
                  </button>
                </li>
              ))}
            </ul>
          </ScrollArea.Autosize>
        )}
      </Popover.Dropdown>
    </Popover>
  )
}

export default NotificationBell
