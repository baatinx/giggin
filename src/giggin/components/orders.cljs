(ns giggin.components.orders
  (:require [giggin.state :as state]))

(defn get-item-price
  [id]
  (get-in @state/gigs [id :price]))

(defn get-net-price
  "item price * quantity"
  [id quantity]
  (* (get-item-price id) quantity))

(defn total
  []
  (reduce +
          (map (fn [[id quantity]] (get-net-price id quantity)) @state/orders)))

(defn orders
  []
  (let [remove-from-orders #(swap! state/orders dissoc %)
        remove-all-orders #(reset! state/orders {})
        get-gig-item #(get-in @state/gigs [%1 %2])]
    [:aside
     (if (empty? @state/orders)
       [:div.empty
        [:div.title "You don't have any orders"]
        [:div.subtitle "Click on a + to add an order"]]
       [:div.order
        [:div.body
         (for [[id quantity] @state/orders]
           [:div.item {:key id}
            [:div.img
             [:img {:src (get-gig-item id :img)
                    :alt (get-gig-item id :title)}]]
            [:div.content
             [:p.title (str (get-gig-item id :title) " \u00D7 " quantity)]]
            [:div.action
             [:div.price (* (get-gig-item id :price) quantity)]
             [:button.btn.btn--link.tooltip
              {:data-tooltip "Remove"
               :on-click #(remove-from-orders id)}
              [:i.icon.icon--cross]]]])]
        [:div.total
         [:hr]
         [:div.item
          [:div.content "Total"]
          [:div.action
           [:div.price (total)]
           [:button.btn.btn--link.tooltip
            {:data-tooltip "Remove All"
             :on-click #(remove-all-orders)}
            [:i.icon.icon--delete]]]]]])]))