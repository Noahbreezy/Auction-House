import "../css/CategoryAuctions.css";
import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { getCategoryById, getAuctionsAssignedToCategory } from "../services/api";
import AuctionCard from "../components/AuctionCard";

function CategoryAuctions() {
  const { id } = useParams();
  const [auctions, setAuctions] = useState([]);
  const [categoryName, setCategoryName] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchData = async () => {
      if (!id) {
        setError("Category ID is missing.");
        setLoading(false);
        return;
      }

      try {
        const category = await getCategoryById(id);
        const auctionsData = await getAuctionsAssignedToCategory(id);

        setCategoryName(category?.name || "Unknown Category");
        setAuctions(auctionsData);
      } catch (err) {
        setError(err.message || "Failed to load category or auctions");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [id]);

  if (loading) return <p>Loading...</p>;
  if (error) return <p>{error}</p>;

  return (
    <div className="category-auctions">
      
      <h2>Auctions in {categoryName}</h2>

      
      <div className="category-auctions-grid">
        {auctions.length > 0 ? (
          auctions.map((auction) => (
            <AuctionCard key={auction.id} auction={auction} />
          ))
        ) : (
          <p>No auctions found in this category.</p>
        )}
      </div>
    </div>
  );
}

export default CategoryAuctions;