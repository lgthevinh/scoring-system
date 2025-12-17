#!/bin/bash
# Script to add remaining teams 11-51 to match the generated schedule

cd "$(dirname "$0")" || exit 1

echo "Adding remaining teams 11-51 to test database..."

sqlite3 src/test/resources/scoring_system.db << 'EOF'
INSERT INTO team (id, name, school, region) VALUES
(11, 'Team Lambda', 'Test School', 'US-CA'),
(12, 'Team Mu', 'Test School', 'US-TX'),
(13, 'Team Nu', 'Test School', 'US-FL'),
(14, 'Team Xi', 'Test School', 'US-NY'),
(15, 'Team Omicron', 'Test School', 'US-IL'),
(16, 'Team Pi', 'Test School', 'US-MA'),
(17, 'Team Rho', 'Test School', 'US-WA'),
(18, 'Team Sigma', 'Test School', 'US-MD'),
(19, 'Team Tau', 'Test School', 'US-NJ'),
(20, 'Team Upsilon', 'Test School', 'US-VA'),
(21, 'Team Phi', 'Test School', 'US-GA'),
(22, 'Team Chi', 'Test School', 'US-PA'),
(23, 'Team Psi', 'Test School', 'US-NC'),
(24, 'Team Omega', 'Test School', 'US-OH'),
(25, 'Team Alpha Prime', 'Test School', 'US-MI'),
(26, 'Team Beta Prime', 'Test School', 'US-AZ'),
(27, 'Team Gamma Prime', 'Test School', 'US-TN'),
(28, 'Team Delta Prime', 'Test School', 'US-IN'),
(29, 'Team Epsilon Prime', 'Test School', 'US-MO'),
(30, 'Team Zeta Prime', 'Test School', 'US-KY'),
(31, 'Team Eta Prime', 'Test School', 'US-AL'),
(32, 'Team Theta Prime', 'Test School', 'US-MN'),
(33, 'Team Iota Prime', 'Test School', 'US-WI'),
(34, 'Team Kappa Prime', 'Test School', 'US-CO'),
(35, 'Team Lambda Prime', 'Test School', 'US-OR'),
(36, 'Team Mu Prime', 'Test School', 'US-UT'),
(37, 'Team Nu Prime', 'Test School', 'US-NV'),
(38, 'Team Xi Prime', 'Test School', 'US-CT'),
(39, 'Team Omicron Prime', 'Test School', 'US-OK'),
(40, 'Team Pi Prime', 'Test School', 'US-LA'),
(41, 'Team Rho Prime', 'Test School', 'US-KS'),
(42, 'Team Sigma Prime', 'Test School', 'US-IA'),
(43, 'Team Tau Prime', 'Test School', 'US-AR'),
(44, 'Team Upsilon Prime', 'Test School', 'US-MS'),
(45, 'Team Phi Prime', 'Test School', 'US-WV'),
(46, 'Team Chi Prime', 'Test School', 'US-NE'),
(47, 'Team Psi Prime', 'Test School', 'US-SD'),
(48, 'Team Omega Prime', 'Test School', 'US-NH'),
(49, 'Team Alpha Double', 'Test School', 'US-RI'),
(50, 'Team Beta Double', 'Test School', 'US-MT'),
(51, 'Team Gamma Double', 'Test School', 'US-DE');
EOF

echo "Checking results..."
sqlite3 src/test/resources/scoring_system.db "SELECT COUNT(*) as teams_1_to_51 FROM team WHERE id BETWEEN 1 AND 51;"

echo "Success! Your match schedule now has complete team data."
