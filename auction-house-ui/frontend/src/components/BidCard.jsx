import React from "react";
import { toast } from "react-toastify";
import "../css/BidCard.css";
import CountdownTimer from "./CountdownTimer";

function BidCard({ auction, onPlaceBid, isAuthenticated, error, isBidding }) {
  const [bidValue, setBidValue] = React.useState("");
  const [localError, setLocalError] = React.useState("");

  // Parse backend date format (dd-MM-yyyy HH:mm:ss)
  const parseAuctionDate = (dateString) => {
    const [datePart, timePart] = dateString.split(' ');
    const [day, month, year] = datePart.split('-');
    const [hours, minutes, seconds] = timePart.split(':');
    
    return new Date(
      parseInt(year),
      parseInt(month) - 1, // Months are 0-indexed
      parseInt(day),
      parseInt(hours),
      parseInt(minutes),
      parseInt(seconds)
    );
  };

  const currentBid = auction.currentBid || auction.startPrice;
  const minBid = currentBid + 0.01;
  const endDate = parseAuctionDate(auction.endTime);
  const isAuctionActive = endDate > new Date();

  const validateBid = (amount) => {
    if (!amount || isNaN(amount)) {
      return "Please enter a valid number";
    }
    if (amount <= 0) {
      return "Bid must be positive";
    }
    if (amount < auction.startPrice) {
      return `Bid must be at least the starting price (€${auction.startPrice.toFixed(2)})`;
    }
    if (amount <= currentBid) {
      return `Bid must be higher than current bid (€${currentBid.toFixed(2)})`;
    }
    return null;
  };

  const handleBid = () => {
    const bidAmount = parseFloat(bidValue);
    const validationError = validateBid(bidAmount);
    
    if (validationError) {
      toast.error(validationError);
      return;
    }

    toast.dismiss(); // Clear previous toasts
    onPlaceBid(bidAmount);
    setBidValue("");
  };

  const handleQuickBid = (increment) => {
    const quickBid = minBid + increment;
    setBidValue(quickBid.toFixed(2));
  };

  return (
    <div className="bid-card">
      <div className="card-header">
        <h3>Place Your Bid</h3>
        <div className="auction-status">
          {isAuctionActive ? (
            <span className="status-active">Active</span>
          ) : (
            <span className="status-closed">Closed</span>
          )}
        </div>
      </div>

      <div className="price-details">
        <div className="price-row">
          <span>Current Bid</span>
          <span className="price-value">€{currentBid.toFixed(2)}</span>
        </div>
        <div className="price-row">
          <span>Starting Price</span>
          <span className="price-value">€{auction.startPrice.toFixed(2)}</span>
        </div>
        <div className="price-row">
          <span>Number of Bidders</span>
          <span className="price-value">{auction.bidders || 0}</span>
        </div>
      </div>

      <div className="countdown-section">
        <p>Time Remaining:</p>
        <CountdownTimer endTime={auction.endTime} />
      </div>

      {isAuctionActive ? (
        isAuthenticated ? (
          <>
            <div className="bid-input-group">
              <div className="input-with-label">
                <label htmlFor="bid-amount">Your Bid (€)</label>
                <input
                  id="bid-amount"
                  type="number"
                  placeholder={`Min €${minBid.toFixed(2)}`}
                  value={bidValue}
                  onChange={(e) => setBidValue(e.target.value)}
                  min={minBid}
                  step="0.01"
                />
              </div>
              <button
                onClick={handleBid}
                className="place-bid-btn"
                disabled={!bidValue || isBidding}
              >
                {isBidding ? (
                  <>
                    <span className="spinner"></span> Placing...
                  </>
                ) : (
                  "Place Bid"
                )}
              </button>
            </div>

            <div className="quick-bid-buttons">
              <p>Quick Bid:</p>
              <div className="quick-bid-options">
                {[1, 5, 10].map((increment) => (
                  <button
                    key={increment}
                    className="quick-bid"
                    onClick={() => handleQuickBid(increment)}
                    type="button"
                  >
                    +€{increment}
                  </button>
                ))}
              </div>
            </div>

            {(error || localError) && (
              <div className="error-message">
                {error || localError}
              </div>
            )}
          </>
        ) : (
          <div className="bid-disabled-overlay">
            <p>
              Please <strong>sign in</strong> to place a bid
            </p>
          </div>
        )
      ) : (
        <div className="auction-closed-message">
          <p>Auction ended on {endDate.toLocaleDateString()}</p>
          <p>No more bids accepted</p>
        </div>
      )}
    </div>
  );
}

export default BidCard;