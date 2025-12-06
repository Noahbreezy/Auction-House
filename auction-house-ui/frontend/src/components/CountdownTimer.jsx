import React, { useState, useEffect } from 'react';

const parseBackendDate = (dateString) => {
  if (!dateString) return new Date();
  
  // Parse "dd-MM-yyyy HH:mm:ss" format
  const [datePart, timePart] = dateString.split(' ');
  const [day, month, year] = datePart.split('-');
  const [hours, minutes, seconds] = timePart.split(':');
  
  return new Date(
    parseInt(year),
    parseInt(month) - 1, // Months are 0-indexed in JS
    parseInt(day),
    parseInt(hours),
    parseInt(minutes),
    parseInt(seconds)
  );
};

const CountdownTimer = ({ endTime }) => {
  const [timeLeft, setTimeLeft] = useState(calculateTimeLeft());

  function calculateTimeLeft() {
    const now = new Date();
    const end = parseBackendDate(endTime);
    
    console.log('Current time:', now.toLocaleString());
    console.log('End time:', end.toLocaleString());
    
    const difference = end - now;
    
    if (difference <= 0) {
      return { expired: true };
    }
    
    return {
      days: Math.floor(difference / (1000 * 60 * 60 * 24)),
      hours: Math.floor((difference / (1000 * 60 * 60)) % 24),
      minutes: Math.floor((difference / 1000 / 60) % 60),
      seconds: Math.floor((difference / 1000) % 60),
      expired: false
    };
  }

  useEffect(() => {
    const timer = setInterval(() => {
      setTimeLeft(calculateTimeLeft());
    }, 1000);
    
    return () => clearInterval(timer);
  }, [endTime]);

  if (timeLeft.expired) {
    return <div className="expired-countdown">Auction Ended</div>;
  }

  return (
    <div className="countdown-timer">
      <div className="countdown-unit">
        <span className="countdown-value">{timeLeft.days}</span>
        <span className="countdown-label">Days</span>
      </div>
      <div className="countdown-unit">
        <span className="countdown-value">{timeLeft.hours}</span>
        <span className="countdown-label">Hours</span>
      </div>
      <div className="countdown-unit">
        <span className="countdown-value">{timeLeft.minutes}</span>
        <span className="countdown-label">Mins</span>
      </div>
      <div className="countdown-unit">
        <span className="countdown-value">{timeLeft.seconds}</span>
        <span className="countdown-label">Secs</span>
      </div>
    </div>
  );
};

export default CountdownTimer;