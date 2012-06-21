#!/bin/bash
ruby stubs.rb profiles/kilkari.profile &> /var/log/kilkari/stubs.log &
echo "Logs can be found @ /var/log/kilkari/stubs.log"
