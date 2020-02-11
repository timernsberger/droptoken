const prefix = '/drop_token';

const fetchApi = (path, options) => fetch(`${prefix}/${path}`, options);

const fetchApiPost = (path, data) => fetchApi(path, {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify(data)
});

const listGames = () => fetchApi('').then(r => r.json());

const createGame = data => fetchApiPost('', data);

const dropPlayer = (gameId, playerId) => fetch(`${prefix}/${gameId}/${playerId}`, { method: 'DELETE' });

function install() {
  document.getElementById('trigger-list-games').addEventListener('click', () => {
    listGames().then(data => document.getElementById('games-list').innerText = data);
  });
  window.api = {
    listGames,
    createGame,
    fetchApi,
    fetchApiPost,
    dropPlayer
  };
}
install();
