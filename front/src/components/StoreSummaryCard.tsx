function StoreSummaryCard({storeSummary}: { storeSummary: HairShopDto }) {

    return (
        <div className="p-4 hover:cursor-pointer hover:bg-gray-200">
            <p>{storeSummary.shopName}</p>
            {/*<p>{storeSummary.summary}</p>*/}
        </div>
    )
}

export default StoreSummaryCard
