import {
  Whisper,
  Dropdown,
  Popover,
  IconButton
} from "rsuite";

import { SlOptionsVertical } from "react-icons/sl";


export default function Menu({menuItems}) {

    const renderMemberActionsMenu = ({ onClose, left, top, className }, ref) => {
        const handleSelect = (eventKey) => {
            onClose();
        };
        return (
        <Popover ref={ref} className={className} style={{ left, top }} full>
            <Dropdown.Menu onSelect={handleSelect}>
                {menuItems.map(item => (
                    item
                ))}
            </Dropdown.Menu>
        </Popover>
        );
    };
    return(
        <>
            <Whisper placement="bottomEnd" trigger="click" rootClose speaker={renderMemberActionsMenu}>
                <IconButton appearance="subtle" icon={<SlOptionsVertical />}/>
            </Whisper>
        </>
    )
}