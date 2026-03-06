import { useState, useEffect } from 'react';
import { notificationAPI } from '../services/api';
import { toast } from 'react-toastify';
import { FiBell, FiCheck, FiCheckCircle, FiTrash2, FiCalendar, FiAlertCircle, FiMessageCircle } from 'react-icons/fi';

const Notifications = () => {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showUnread, setShowUnread] = useState(false);

  useEffect(() => { fetchNotifications(); }, [showUnread]);

  const fetchNotifications = async () => {
    try {
      const res = showUnread
        ? await notificationAPI.getUnread()
        : await notificationAPI.getAll();
      setNotifications(res.data.data || []);
    } catch (err) {
      toast.error('Failed to fetch notifications');
    } finally {
      setLoading(false);
    }
  };

  const handleMarkRead = async (id) => {
    try {
      await notificationAPI.markAsRead(id);
      fetchNotifications();
    } catch (err) {
      toast.error('Failed to mark as read');
    }
  };

  const handleMarkAllRead = async () => {
    try {
      await notificationAPI.markAllAsRead();
      fetchNotifications();
      toast.success('All notifications marked as read');
    } catch (err) {
      toast.error('Failed');
    }
  };

  const handleDelete = async (id) => {
    try {
      await notificationAPI.delete(id);
      fetchNotifications();
    } catch (err) {
      toast.error('Failed to delete');
    }
  };

  const getIcon = (type) => {
    switch (type) {
      case 'BOOKING_APPROVED': return <FiCheckCircle className="w-5 h-5 text-green-500" />;
      case 'BOOKING_REJECTED': return <FiCalendar className="w-5 h-5 text-red-500" />;
      case 'TICKET_STATUS_CHANGED': return <FiAlertCircle className="w-5 h-5 text-blue-500" />;
      case 'NEW_COMMENT': return <FiMessageCircle className="w-5 h-5 text-purple-500" />;
      case 'TICKET_ASSIGNED': return <FiAlertCircle className="w-5 h-5 text-orange-500" />;
      default: return <FiBell className="w-5 h-5 text-gray-500" />;
    }
  };

  if (loading) {
    return <div className="flex justify-center py-12"><div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div></div>;
  }

  return (
    <div className="max-w-3xl mx-auto px-4 py-6">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Notifications</h1>
          <p className="text-gray-500 text-sm mt-1">Stay updated on your activities</p>
        </div>
        <div className="flex items-center space-x-3">
          <button onClick={() => setShowUnread(!showUnread)}
            className={`text-sm px-3 py-1 rounded-full ${showUnread ? 'bg-blue-600 text-white' : 'bg-white text-gray-600 border'}`}>
            {showUnread ? 'Unread Only' : 'All'}
          </button>
          <button onClick={handleMarkAllRead}
            className="flex items-center space-x-1 text-sm text-blue-600 hover:text-blue-700">
            <FiCheck className="w-4 h-4" /> <span>Mark all read</span>
          </button>
        </div>
      </div>

      {notifications.length === 0 ? (
        <div className="text-center py-12 bg-white rounded-xl shadow-sm">
          <FiBell className="w-12 h-12 text-gray-300 mx-auto mb-3" />
          <p className="text-gray-400">No notifications</p>
        </div>
      ) : (
        <div className="space-y-2">
          {notifications.map(notif => (
            <div key={notif.id}
              className={`bg-white rounded-xl shadow-sm p-4 flex items-start space-x-4 ${!notif.read ? 'border-l-4 border-blue-500' : ''}`}>
              <div className="mt-0.5">{getIcon(notif.type)}</div>
              <div className="flex-1">
                <p className={`text-sm ${!notif.read ? 'font-semibold text-gray-900' : 'text-gray-700'}`}>
                  {notif.title}
                </p>
                <p className="text-sm text-gray-500 mt-0.5">{notif.message}</p>
                <p className="text-xs text-gray-400 mt-1">
                  {new Date(notif.createdAt).toLocaleString()}
                </p>
              </div>
              <div className="flex items-center space-x-1">
                {!notif.read && (
                  <button onClick={() => handleMarkRead(notif.id)}
                    className="p-1 text-blue-500 hover:bg-blue-50 rounded" title="Mark as read">
                    <FiCheck className="w-4 h-4" />
                  </button>
                )}
                <button onClick={() => handleDelete(notif.id)}
                  className="p-1 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded" title="Delete">
                  <FiTrash2 className="w-4 h-4" />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Notifications;
