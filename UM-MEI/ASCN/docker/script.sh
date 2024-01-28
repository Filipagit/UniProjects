#!/bin/bash

if [ "$MIGRATE" == "true" ]; then
  echo ">>> Running migration"
  php artisan migrate --seed
fi

echo ">>> Starting app..."
php artisan serve --host=0.0.0.0 