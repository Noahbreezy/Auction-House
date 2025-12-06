import '../css/Auctions.css';
import { useEffect, useState } from 'react';
import AuctionCard from '../components/AuctionCard';
import { getAllAuctions } from '../services/api.js'; 


function Auctions() {
  const [auctions, setAuctions] = useState([]);

  useEffect(() => {
    const load = async () => {
      const result = await getAllAuctions();
      setAuctions(result);
    };
    load();
  }, []);

return (
  <div className="auction-list">
    <h2>All Auctions</h2> 
    {auctions.length > 0 ? (
      <div className="auction-grid">
        {auctions.map((a) => (
          <AuctionCard auction={a} key={a.id} />
        ))}
      </div>
    ) : (
      <p>No auctions available.</p>
    )}
  </div>
);
}

export default Auctions;
