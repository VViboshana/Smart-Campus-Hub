import { useState, useRef, useEffect } from 'react';
import { FiMessageSquare, FiX, FiSend, FiChevronDown } from 'react-icons/fi';
import { chatAPI } from '../../services/api';

const ChatBot = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState([
    {
      id: 1,
      type: 'bot',
      text: '👋 Hi! I\'m the Smart Campus Hub assistant. I can help you with bookings, resources, tickets, and more. What would you like to know?',
      suggestions: ['What can you do?', 'How do I book a resource?', 'What resources are available?', 'How do I create a ticket?'],
    },
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const messagesEndRef = useRef(null);
  const inputRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  useEffect(() => {
    if (isOpen && inputRef.current) {
      inputRef.current.focus();
    }
  }, [isOpen]);

  const sendMessage = async (text) => {
    if (!text.trim()) return;

    const userMsg = {
      id: Date.now(),
      type: 'user',
      text: text.trim(),
    };
    setMessages((prev) => [...prev, userMsg]);
    setInput('');
    setLoading(true);

    try {
      const res = await chatAPI.sendMessage(text.trim());
      const data = res.data.data;
      const botMsg = {
        id: Date.now() + 1,
        type: 'bot',
        text: data.reply,
        suggestions: data.suggestions || [],
      };
      setMessages((prev) => [...prev, botMsg]);
    } catch (err) {
      const errorMsg = {
        id: Date.now() + 1,
        type: 'bot',
        text: '❌ Sorry, something went wrong. Please try again.',
        suggestions: ['What can you do?'],
      };
      setMessages((prev) => [...prev, errorMsg]);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    sendMessage(input);
  };

  const handleSuggestionClick = (suggestion) => {
    sendMessage(suggestion);
  };

  const renderMarkdown = (text) => {
    // Simple markdown rendering for bold text and line breaks
    return text
      .split('\n')
      .map((line, i) => {
        const formattedLine = line.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
        return (
          <span key={i}>
            <span dangerouslySetInnerHTML={{ __html: formattedLine }} />
            {i < text.split('\n').length - 1 && <br />}
          </span>
        );
      });
  };

  return (
    <>
      {/* Chat Toggle Button */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        className={`fixed bottom-6 right-6 z-50 w-14 h-14 rounded-full shadow-lg flex items-center justify-center transition-all duration-300 hover:scale-110 ${
          isOpen
            ? 'bg-gray-600 hover:bg-gray-700'
            : 'bg-blue-600 hover:bg-blue-700 animate-bounce'
        }`}
        style={{ animationIterationCount: isOpen ? 0 : 3 }}
        title={isOpen ? 'Close chat' : 'Chat with assistant'}
      >
        {isOpen ? (
          <FiX className="w-6 h-6 text-white" />
        ) : (
          <FiMessageSquare className="w-6 h-6 text-white" />
        )}
      </button>

      {/* Chat Window */}
      {isOpen && (
        <div className="fixed bottom-24 right-6 z-50 w-96 max-w-[calc(100vw-2rem)] bg-white rounded-2xl shadow-2xl border border-gray-200 flex flex-col overflow-hidden"
             style={{ height: '520px' }}>
          {/* Header */}
          <div className="bg-gradient-to-r from-blue-600 to-blue-700 px-4 py-3 flex items-center justify-between flex-shrink-0">
            <div className="flex items-center gap-3">
              <div className="w-9 h-9 bg-white/20 rounded-full flex items-center justify-center">
                <FiMessageSquare className="w-5 h-5 text-white" />
              </div>
              <div>
                <h3 className="text-white font-semibold text-sm">Campus Assistant</h3>
                <p className="text-blue-100 text-xs">Always here to help</p>
              </div>
            </div>
            <button
              onClick={() => setIsOpen(false)}
              className="text-white/70 hover:text-white transition-colors"
            >
              <FiChevronDown className="w-5 h-5" />
            </button>
          </div>

          {/* Messages */}
          <div className="flex-1 overflow-y-auto px-4 py-3 space-y-3 bg-gray-50">
            {messages.map((msg) => (
              <div key={msg.id}>
                <div
                  className={`flex ${msg.type === 'user' ? 'justify-end' : 'justify-start'}`}
                >
                  <div
                    className={`max-w-[85%] px-3.5 py-2.5 rounded-2xl text-sm leading-relaxed ${
                      msg.type === 'user'
                        ? 'bg-blue-600 text-white rounded-br-md'
                        : 'bg-white text-gray-800 border border-gray-200 rounded-bl-md shadow-sm'
                    }`}
                  >
                    {msg.type === 'bot' ? renderMarkdown(msg.text) : msg.text}
                  </div>
                </div>
                {/* Suggestions */}
                {msg.type === 'bot' && msg.suggestions && msg.suggestions.length > 0 && (
                  <div className="flex flex-wrap gap-1.5 mt-2 ml-1">
                    {msg.suggestions.map((suggestion, idx) => (
                      <button
                        key={idx}
                        onClick={() => handleSuggestionClick(suggestion)}
                        disabled={loading}
                        className="px-2.5 py-1 text-xs bg-blue-50 text-blue-700 border border-blue-200 rounded-full hover:bg-blue-100 hover:border-blue-300 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                      >
                        {suggestion}
                      </button>
                    ))}
                  </div>
                )}
              </div>
            ))}
            {loading && (
              <div className="flex justify-start">
                <div className="bg-white border border-gray-200 rounded-2xl rounded-bl-md px-4 py-3 shadow-sm">
                  <div className="flex gap-1.5">
                    <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: '0ms' }} />
                    <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: '150ms' }} />
                    <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: '300ms' }} />
                  </div>
                </div>
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>

          {/* Input */}
          <form onSubmit={handleSubmit} className="px-3 py-2.5 bg-white border-t border-gray-200 flex-shrink-0">
            <div className="flex items-center gap-2">
              <input
                ref={inputRef}
                type="text"
                value={input}
                onChange={(e) => setInput(e.target.value)}
                placeholder="Type your message..."
                disabled={loading}
                className="flex-1 px-3.5 py-2 text-sm bg-gray-100 border border-gray-200 rounded-full focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent disabled:opacity-50 placeholder-gray-400"
              />
              <button
                type="submit"
                disabled={!input.trim() || loading}
                className="w-9 h-9 bg-blue-600 text-white rounded-full flex items-center justify-center hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex-shrink-0"
              >
                <FiSend className="w-4 h-4" />
              </button>
            </div>
          </form>
        </div>
      )}
    </>
  );
};

export default ChatBot;
