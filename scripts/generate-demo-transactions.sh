#!/usr/bin/env bash
#
# Generates demo transaction history by driving the running REST API, so every
# entry is a valid double-entry ledger transaction (the app enforces balances,
# TSIDs, the external-logging prerequisite, and EUR-only debit).
#
# Prerequisite: the backend is running (default http://localhost:8080) and the
# johndoe / janedoe customers + accounts are seeded (Liquibase).
#
# Usage: scripts/generate-demo-transactions.sh [BASE_URL] [COUNT_PER_CUSTOMER]

set -uo pipefail

BASE_URL="${1:-http://localhost:8080}"
COUNT="${2:-100}"

DESCS=(Salary Refund Groceries Rent ATM Bonus Utilities Transfer Dining Shopping Travel Insurance)

post_credit() { # username account amount description
  curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/account/credit" \
    -H "X-Username: $1" -H "X-Account-Code: $2" -H "Content-Type: application/json" \
    -d "{\"amount\":\"$3\",\"description\":\"$4\"}"
}

post_debit() { # username account amount description
  curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/account/debit" \
    -H "X-Username: $1" -H "X-Account-Code: $2" -H "Content-Type: application/json" \
    -d "{\"amount\":\"$3\",\"description\":\"$4\"}"
}

post_exchange() { # username source target amount
  curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/exchange" \
    -H "X-Username: $1" -H "X-Source-Account-Code: $2" -H "X-Target-Account-Code: $3" \
    -H "Content-Type: application/json" -d "{\"amount\":\"$4\"}"
}

generate() { # username eur_account target_account_a target_account_b
  local user="$1" eur="$2" ta="$3" tb="$4"
  local balance made=0

  post_credit "$user" "$eur" "5000.00" "Opening deposit" >/dev/null
  balance=500000; made=1

  while [ "$made" -lt "$COUNT" ]; do
    local amt_cents=$(( (RANDOM % 11600) + 500 ))
    local amt; amt=$(printf "%d.%02d" $((amt_cents / 100)) $((amt_cents % 100)))
    local desc=${DESCS[$((RANDOM % ${#DESCS[@]}))]}
    local r=$(( RANDOM % 100 )) code op

    if [ "$r" -lt 45 ] || [ "$balance" -lt "$amt_cents" ]; then
      op=deposit; code=$(post_credit "$user" "$eur" "$amt" "$desc")
    elif [ "$r" -lt 73 ]; then
      op=withdraw; code=$(post_debit "$user" "$eur" "$amt" "$desc")
    else
      local tgt=$ta; [ $((RANDOM % 2)) -eq 0 ] && tgt=$tb
      op=exchange; code=$(post_exchange "$user" "$eur" "$tgt" "$amt")
    fi

    if [ "$code" = "200" ]; then
      case "$op" in
        deposit) balance=$((balance + amt_cents)) ;;
        *)       balance=$((balance - amt_cents)) ;;
      esac
      made=$((made + 1))
    else
      # external-logging hiccup (502) or transient error: pause, then a deposit
      # fallback keeps progress without needing the external call
      sleep 1
      if [ "$(post_credit "$user" "$eur" "$amt" "Refund")" = "200" ]; then
        balance=$((balance + amt_cents)); made=$((made + 1))
      fi
    fi
  done

  printf '%s: %d transactions on account %s (final EUR balance ~%d.%02d)\n' \
    "$user" "$made" "$eur" $((balance / 100)) $((balance % 100))
}

echo "Generating $COUNT transactions each against $BASE_URL ..."
generate johndoe 1000041 1000042 1000043
generate janedoe 1000051 1000052 1000053
echo "Done."
